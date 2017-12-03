package com.akka.eventsourcing;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class PersistentActorExample {
	public static void main(String[] args) {
		PersistentActorExample ex = new PersistentActorExample();
		ex.createEvents();
	}
	
	private void createEvents() {
		final ActorSystem system = ActorSystem.create("akka");
/*
		final ActorRef persistentActor = system.actorOf(
				Props.create(ExamplePersistenceActorWithJava8.class),"persistentActor-4-java");
*/

	final ActorRef persistentActor = system.actorOf(
				Props.create(ExamplePersistenceActor.class),"persistentActor-java");

		
		/*final ActorRef persistentActor = system.actorOf(
				Props.create(WithdeferAsync.class),"persistentActor-4-java");
		*/
		
		persistentActor.tell(new Command("foo"), null);
		persistentActor.tell(new Command("baz"), null);
		persistentActor.tell(new Command("bar"), null);
		persistentActor.tell("snap", null);
		persistentActor.tell(new Command("buzz"), null);
		persistentActor.tell("print", null);
		persistentActor.tell(new Shutdown(), null);
	
	}
	
}
