package com.bizruntime.akka;

import java.io.Serializable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;

public class HelloWorlAkka {
	
	public static class Greet implements Serializable{}
	
	public static class WhoToGreet implements Serializable{
		
		private final String who;

		public WhoToGreet(String who) {
			this.who = who;
		}
	}
	
	public static class Greeting implements Serializable{
		
		private final String message;

		public Greeting(String message) {
			this.message = message;
		}
	}
	
	public static class Greeter extends UntypedActor{
		String greeting;
		
		
		@Override
		public void onReceive(Object message) throws Exception {
			if(message instanceof WhoToGreet){
				greeting = "Hello, " + ((WhoToGreet)message).who;
				getSender().tell(greeting, getSelf());
				
			}
			else if(message instanceof Greet)
				
				getSender().tell(new Greeting(greeting), getSelf());
			else
				unhandled(message);
		}
	}
	
	public static class GreetPrinter extends UntypedActor{
		@Override
		public void onReceive(Object message) throws Exception {
			String m;
			if(message instanceof Greeting)
				m = ((Greeting)message).message;
			
		}
	}
	
	public void doSomething(){
		
		final ActorSystem system = ActorSystem.create("helloAkka");
		
		ActorRef greeter = system.actorOf(Props.create(Greeter.class),"greeter");
		
		Inbox inbox = Inbox.create(system);
		
		greeter.tell(new WhoToGreet("akka"), ActorRef.noSender());
		
		try {
		inbox.send(greeter, new Greet());

		final Greeting greeting1 = (Greeting) inbox.receive(Duration.create(5, TimeUnit.MINUTES));
		
		greeter.tell(new WhoToGreet("typesafe"), ActorRef.noSender());
		
		inbox.send(greeter, new Greet());
		final Greeting greeting2 = (Greeting) inbox.receive(Duration.create(10, "seconds"));
		
		ActorRef greetPrinter = system.actorOf(Props.create(GreetPrinter.class));
		system.scheduler().schedule(Duration.Zero(), Duration.create(1, TimeUnit.SECONDS), greeter,  new Greet(), system.dispatcher(), greetPrinter);
		
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		HelloWorlAkka sengGreeting = new HelloWorlAkka();
		sengGreeting.doSomething();
	}
	
	
}
