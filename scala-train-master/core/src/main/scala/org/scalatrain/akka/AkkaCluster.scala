package org.scalatrain.akka

import akka.cluster._
import akka.cluster.ClusterEvent._
import com.typesafe.config.ConfigFactory
import akka.actor._
import scala.concurrent.duration._

class SimpleClusterListener extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  // Cluster.get(system).joinSeedNodes
  cluster.registerOnMemberUp {
    if (cluster.selfRoles.contains("frontend")) {
      context.system.actorOf(Props[TransformationFrontend],
        name = "frontend")
    }
    if (cluster.selfRoles.contains("backend")) {
      context.system.actorOf(Props[TransformationBackend],
        name = "backend")
    }
  }

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {} with roles {}", member.address, member.roles)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case _: MemberEvent => // ignore
  }
}


case class TransformationJob(text: String)

case class TransformationResult(text: String)

case class JobFailed(reason: String, job: TransformationJob)

case object BackendRegistration

class TransformationBackend extends Actor {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, MemberUp
  // re-subscribe when restart
  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case TransformationJob(text) =>
      sender() ! TransformationResult(text.toUpperCase)
      println(s"Got $text to process")
    case state: CurrentClusterState =>
      state.members.filter(_.status == MemberStatus.Up) foreach register
    case MemberUp(m) => register(m)
      println(s"Memeber is up ${m.address}")
  }

  def register(member: Member): Unit =
    if (member.hasRole("frontend"))
      context.actorSelection(RootActorPath(member.address) / "user" / "frontend") !
        BackendRegistration
}

class TransformationFrontend extends Actor {

  var backends = IndexedSeq.empty[ActorRef]
  var jobCounter = 0

  import context.dispatcher

  context.system.scheduler.schedule(10.seconds, 1.second, self, TransformationJob("test"))

  def receive = {
    case job: TransformationJob if backends.isEmpty =>
      sender() ! JobFailed("Service unavailable, try again later", job)

    case job: TransformationJob =>
      jobCounter += 1
      val ref = backends(jobCounter % backends.size)
      ref forward job
      println(s"Forwarded job $jobCounter to backend $ref")
    case TransformationResult(res) => println(s"Got result $res")
    case BackendRegistration if !backends.contains(sender()) =>
      println(s"Got backend $sender")
      context watch sender()
      backends = backends :+ sender()

    case Terminated(a) =>
      backends = backends.filterNot(_ == a)
  }
}


object AkkaCluster {
  def main(args: Array[String]): Unit = {
    if (args.isEmpty)
      startup(Seq("2551", "2552", "0"))
    else
      startup(args)
  }

  def startup(ports: Seq[String]): Unit = {
    ports foreach { port =>
      // Override the configuration of the port
      val config = ConfigFactory.parseString(
        s"""
           |akka {
           | actor {
           |   provider = "akka.cluster.ClusterActorRefProvider"
           | }
           | remote {
           |   log-remote-lifecycle-events = off
           |   netty.tcp {
           |     hostname = "127.0.0.1"
           |     port = $port
            |   }
            | }
            |
            | cluster {
            |   seed-nodes = [
            |     "akka.tcp://ClusterSystem@127.0.0.1:2551",
            |     "akka.tcp://ClusterSystem@127.0.0.1:2552"]
            |
            |   auto-down-unreachable-after = 10s
            |
            |   roles = ["backend"]
            | }
            |}
        """.stripMargin).withFallback(ConfigFactory.load())

      // Create an Akka system
      val system = ActorSystem("ClusterSystem", config)
      // Create an actor that handles cluster domain events
      system.actorOf(Props[SimpleClusterListener], name = "clusterListener")
    }
  }

}