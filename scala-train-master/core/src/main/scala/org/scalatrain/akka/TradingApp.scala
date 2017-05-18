package org.scalatrain.akka

import akka.actor.SupervisorStrategy.{Escalate, Stop, Restart, Resume}
import akka.actor._
import akka.pattern._
import akka.util.Timeout

import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.Random

case class Trade(id: Int, notional: Int)

/**
 * Trades should be processed concurrently.
 * Trades with same id should be `process`ed sequentially
 */
class TradingSupervisor extends Actor {
  def receive = {
    case t: Trade =>
  }
}

class ServiceActor extends Actor {
  def receive = {
    case t: Trade =>
      if (Random.nextBoolean()) sys.error(s"Ops! $t") else sender() ! t
  }
}

class TradingActor extends Actor {

  def process(t: Trade): Trade = {
    Thread.sleep(1000)
    t
  }

  def receive = {
    case t: Trade =>
      val p = process(t)
  }
}


/**
 * Initialization patterns
 * constructor
 * preStart
 * message passing
 *
 */
object TradingApp extends App {

  val system = ActorSystem()

  import system.dispatcher
  implicit val timeout = Timeout(1.second)

  val trader = system.actorOf(Props(classOf[TradingSupervisor]))

  val f = trader ! Trade(Random.nextInt(5), 1)
//  val f = Future.traverse(1 to 10)(n => trader ? Trade(Random.nextInt(5), n))

//  f.onComplete {
//    case r => println(r)
//  }
}
