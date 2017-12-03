package com.akka.persistent.view;

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

	final ActorRef persistentActor = system.actorOf(
				Props.create(PersistenceActorView.class),"persistentActorView");

		
	persistentActor.tell("view of other persistent actor received", ActorRef.noSender());
	
	
	}
	
}
