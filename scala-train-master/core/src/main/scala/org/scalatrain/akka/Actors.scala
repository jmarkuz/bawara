package org.scalatrain.akka

import java.util.concurrent.{ConcurrentLinkedQueue, Executors}

import scala.collection.mutable
import scala.concurrent.{Future, Promise}

/**
 * Actor guaranties messages delivery order.
 *
 * State, Behavior, Mailbox
 *
 * at-most-once delivery, i.e. no guaranteed delivery
 * message ordering per sender–receiver pair
 *
 * at-most-once
 * at-least-once
 * exactly-once
 *
 * http://www.infoq.com/articles/no-reliable-messaging
 *
 * Actor A1 sends messages M1, M2, M3 to A2
 * Actor A3 sends messages M4, M5, M6 to A2

 * If M1 is delivered it must be delivered before M2 and M3
 * If M2 is delivered it must be delivered before M3
 * If M4 is delivered it must be delivered before M5 and M6
 * If M5 is delivered it must be delivered before M6
 * A2 can see messages from A1 interleaved with messages from A3
 * Since there is no guaranteed delivery, any of the messages may be dropped, i.e. not arrive at A2
 */
object Actors {

  val tp = Executors.newFixedThreadPool(10)
  val actors = mutable.HashSet[Actor]()

  trait Actor {
    val mailbox = new ConcurrentLinkedQueue[(Any, Actor)]()
    def receive: PartialFunction[(Any, Actor), Unit]
    def tell(msg: Any, sender: Actor): Unit = mailbox.add(msg -> sender)
    def ask(msg: Any): Future[Any] = {
      val p = Promise[Any]()
      mailbox.add(msg -> new FutureActor(p))
      p.future
    }
  }

  class FutureActor(val p: Promise[Any]) extends Actor {

    override def tell(msg: Any, sender: Actor) = p.success(msg)

    override def receive: PartialFunction[(Any, Actor), Unit] = {
      case _ =>
    }
  }

  class Greeter extends Actor {
    var greeting = "Hi!"

    def receive = {
      case (WhoToGreet(who), _) => greeting = s"hello, $who"
      case (Greet, sender)      => sender.tell(Greeting(greeting), this) // Send the current greeting back to the sender
    }
  }

  class GreetPrinter extends Actor {
    def receive = {
      case (Greeting(message), _) => println(message)
    }
  }


  def main(args: Array[String]) {

    val greeter = new Greeter()
    val printer = new GreetPrinter()
    actors += greeter
    actors += printer

    greeter.tell(Greet, printer)
    greeter.tell(WhoToGreet("Alex"), null)
    greeter.tell(Greet, printer)

    val f = greeter.ask(Greet)

    tp.submit(new Runnable {
      override def run(): Unit = {
        while (true) {
          actors.foreach { a =>
            val msg = a.mailbox.poll()
            if (msg != null) a.receive(msg)
          }
          Thread.sleep(100)
        }
      }
    })
  }

}
