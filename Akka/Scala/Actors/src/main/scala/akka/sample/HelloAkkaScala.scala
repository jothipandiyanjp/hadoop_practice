package akka.sample
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Actor 
import akka.actor.Inbox 


import scala.concurrent.duration._

case object Greet
case class WhoToGreet(who: String)
case class Greeting(message: String)

class Greeter extends Actor {
  var greeting = ""

  def receive = {
    case WhoToGreet(who) => greeting = s"hello, $who"
    case Greet => sender ! Greeting(greeting) 
  }
}

object HelloAkkaScala extends App {

  val system = ActorSystem("helloakka")

  val greeter = system.actorOf(Props[Greeter], "greeter")

  val inbox = Inbox.create(system)

  greeter.tell(WhoToGreet("akka"), ActorRef.noSender)

  inbox.send(greeter, Greet)

  val Greeting(message1) = inbox.receive(5.seconds)
  println(s"Greeting: $message1")

  greeter.tell(WhoToGreet("typesafe"),ActorRef.noSender)
  inbox.send(greeter, Greet)
  val Greeting(message2) = inbox.receive(5.seconds)
  println(s"Greeting: $message2")

  val greetPrinter = system.actorOf(Props[GreetPrinter])
  system.scheduler.schedule(0.seconds, 1.second, greeter, Greet)(system.dispatcher, greetPrinter)
  
}

class GreetPrinter extends Actor {
  def receive = {
    case Greeting(message) => println(message)
  }
}