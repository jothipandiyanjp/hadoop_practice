package akka.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Example {
	
	public void sendMessages(){
		ActorSystem system =ActorSystem.create("akka-system");
		ActorRef destination = system.actorOf(Props.create(MyDestination.class), "destination");
		ActorRef ref = system.actorOf(Props.create(MyPersistentActor.class,system.actorSelection(destination.path())),"sender");
		ref.tell("testing", null);
		
	}
	
	public static void main(String[] args) {
		Example example = new Example();
		example.sendMessages();
	}
}
