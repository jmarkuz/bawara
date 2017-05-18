package org.scalatrain.akka

import akka.actor._
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}


class Supervisor extends Actor {

  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._

  import scala.concurrent.duration._

  /**
   * Resume the subordinate, keeping its accumulated internal state
   * Restart the subordinate, clearing out its accumulated internal state
   * Stop the subordinate permanently
   * Escalate the failure, thereby failing itself
   */
  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: ArithmeticException => Resume
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception => Escalate
    }

  def receive = {
    case p: Props => sender() ! context.actorOf(p)
  }
}

class Supervisor2 extends Actor {

  import akka.actor.SupervisorStrategy._

  import scala.concurrent.duration._

  /**
   * Resume the subordinate, keeping its accumulated internal state
   * Restart the subordinate, clearing out its accumulated internal state
   * Stop the subordinate permanently
   * Escalate the failure, thereby failing itself
   */
  override val supervisorStrategy =
    AllForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: ArithmeticException => Resume
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception => Escalate
    }

  def receive = {
    case p: Props => sender() ! context.actorOf(p)
  }
}


class Child extends Actor {
  var state = 0

  def receive = {
    case ex: Exception => throw ex
    case x: Int => state = x
    case "get" => sender() ! state
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = super.preStart()

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = super.postStop()

  @throws[Exception](classOf[Exception])
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = super.preRestart(reason, message)

  @throws[Exception](classOf[Exception])
  override def postRestart(reason: Throwable): Unit = {
    println(s"actor $self restarted")
    super.postRestart(reason)
  }
}


class FaultHandlingDocSpec(_system: ActorSystem) extends TestKit(_system)
with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  "A supervisor" must {

    "apply the one for one strategy for its child" in {
      val supervisor = system.actorOf(Props[Supervisor], "supervisor")

      supervisor ! Props[Child]
      val child = expectMsgType[ActorRef] // retrieve answer from TestKit’s testActor

      child ! 42 // set state to 42
      child ! "get"
      expectMsg(42)

      child ! new ArithmeticException // crash it
      child ! "get"
      expectMsg(42)

      child ! new NullPointerException // crash it harder
      child ! "get"
      expectMsg(0)

      watch(child) // have testActor watch “child”
      child ! new IllegalArgumentException // break it
      expectMsgPF() { case Terminated(`child`) => () }
    }


    "apply the all for one strategy for its children" in {
      // All For One Strategy
      val supervisor2 = system.actorOf(Props[Supervisor2], "supervisor2")

      supervisor2 ! Props[Child]
      supervisor2 ! Props[Child]
      supervisor2 ! Props[Child]

      val child1 = expectMsgType[ActorRef]
      expectMsgType[ActorRef]
      expectMsgType[ActorRef]

      system.actorSelection("/user/supervisor2/*") ! 42
      system.actorSelection("/user/supervisor2/*") ! "get"
      expectMsg(42)
      expectMsg(42)
      expectMsg(42)


      child1 ! new NullPointerException // crash it harder
      Thread.sleep(1000) // otherwise, some actors may not be restarted yet
      system.actorSelection("/user/supervisor2/*") ! "get"
      expectMsg(0)
      expectMsg(0)
      expectMsg(0)
    }

  }


  def this() = this(ActorSystem("FaultHandlingDocSpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
}