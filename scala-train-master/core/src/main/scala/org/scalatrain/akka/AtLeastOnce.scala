package org.scalatrain.akka

import akka.actor._
import akka.persistence._

import scala.concurrent.duration._
import scala.util.Random

case class Msg(deliveryId: Long, s: String)
case class Confirm(deliveryId: Long)

sealed trait Event
case class MsgSent(s: String) extends Event
case class MsgConfirmed(deliveryId: Long) extends Event

class MyPersistentActor(destination: ActorPath)
  extends PersistentActor with AtLeastOnceDelivery {


  override def redeliverInterval: FiniteDuration = 1.second

  def receiveCommand: Receive = {
    case "clean"           => deleteMessages(lastSequenceNr)
    case s: String           => persist(MsgSent(s))(updateState)
    case Confirm(deliveryId) => persist(MsgConfirmed(deliveryId))(updateState)
  }

  def receiveRecover: Receive = {
    case evt: Event => updateState(evt)
  }

  def updateState(evt: Event): Unit = evt match {
    case MsgSent(s) =>
      deliver(destination, deliveryId => Msg(deliveryId, s))

    case MsgConfirmed(deliveryId) =>
      confirmDelivery(deliveryId)
      println(s"Confirmed delivery of $deliveryId")
  }
}

class MyDestination extends Actor {
  def receive = {
    case Msg(deliveryId, s) =>
      if (Random.nextBoolean()) {
        sender() ! Confirm(deliveryId)
        println(s"Confirm $s $deliveryId")
      } else println(s"Drop $s $deliveryId")
  }
}

object AtLeastOnce extends App {
  val system = ActorSystem("example")
  val receiver = system.actorOf(Props[MyDestination], "receiver")
  val sender = system.actorOf(Props(classOf[MyPersistentActor], receiver.path), "sender")

  sender ! "hello1"
  sender ! "hello2"
  sender ! "hello3"

  Thread.sleep(5000)
  system.shutdown()
}
