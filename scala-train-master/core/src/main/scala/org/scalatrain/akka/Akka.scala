package org.scalatrain.akka

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import scala.concurrent.duration._

case object Greet

case class WhoToGreet(who: String)

case class Greeting(message: String)

case class CreateChild(name: String)

class Greeter extends Actor {
  var greeting = ""

  def receive = {
    case WhoToGreet(who) => greeting = s"hello, $who"
    case Greet =>
      self ! Greeting("hi")
      sender ! Greeting(greeting) // Send the current greeting back to the sender
    case CreateChild(name) =>
      val child = context.actorOf(Props[Greeter], name)
      //  Lifecycle Monitoring
      context.watch(child)
      println(child.path)
    case Terminated(child) =>
      println(s"Actor ${child.path} terminated")
      context.unwatch(child)
  }

  override def unhandled(message: Any): Unit = {
    println(s"Unhandled $message")
    super.unhandled(message)
  }
}

// prints a greeting
class GreetPrinter extends Actor {
  def receive = {
    case Greeting(message) => println(message)
  }
}

object HelloAkkaScala extends App {

  val system = ActorSystem("helloakka")

  system.dispatchers

  val greeter: ActorRef = system.actorOf(Props[Greeter], "greeter")

  // Create an "actor-in-a-box"
  val inbox = Inbox.create(system)

  greeter.tell(WhoToGreet("akka"), ActorRef.noSender)

  inbox.send(greeter, Greet)

  // Wait 5 seconds for the reply with the 'greeting' message
  val Greeting(message1) = inbox.receive(5.seconds)
  println(s"Greeting: $message1")

  // Change the greeting and ask for it again
  greeter.tell(WhoToGreet("typesafe"), ActorRef.noSender)
  greeter ! WhoToGreet("typesafe")

  inbox.send(greeter, Greet)
  val Greeting(message2) = inbox.receive(5.seconds)
  println(s"Greeting: $message2")


  // Ask pattern

  import system.dispatcher

  implicit val timeout = Timeout(1.second)
  val f1 = greeter.ask(Greet)(Timeout(1.second))
  val f2 = greeter ? Greet
  f1.onSuccess {
    case (Greeting(m)) => println(s"Greeted from ask: $m")
  }

  greeter ! CreateChild("child")
  private val child = system.actorSelection("akka://helloakka/user/greeter/child")
  child ! WhoToGreet("Hello Child")

  child ! PoisonPill

  val greetPrinter = system.actorOf(Props[GreetPrinter])

  // after zero seconds, send a Greet message every second to the greeter with a sender of the greetPrinter
  system.scheduler.schedule(0.seconds, 1.second, greeter, Greet)(system.dispatcher, greetPrinter)

  // the guardian actor for all user-created top-level actors;
  // actors created using ActorSystem.actorOf are found below this one.
  system.actorSelection("/user")
  // the guardian actor for all system-created top-level actors,
  // e.g. logging listeners or actors automatically deployed by configuration
  // at the start of the actor system.
  system.actorSelection("/system")
  // the dead letter actor, which is where all messages sent to stopped or non-existing actors
  // are re-routed (on a best-effort basis: messages may be lost even within the local JVM).
  system.actorSelection("/deadLetters")
  // the guardian for all short-lived system-created actors,
  // e.g. those which are used in the implementation of ActorRef.ask.
  system.actorSelection("/temp")
  // an artificial path below which all actors reside
  // whose supervisors are remote actor references
  system.actorSelection("/remote")
}

