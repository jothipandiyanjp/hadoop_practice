package akka.event.app;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.DeadLetter;
import akka.actor.Props;
import akka.event.actors.DeadLetterActor;


public class DeadLetterApp {

	public void subscribe(){
		final ActorSystem system = ActorSystem.create("DeadLetters");
		
		final ActorRef actor = system.actorOf(Props.create(DeadLetterActor.class));		
		system.eventStream().subscribe(actor, DeadLetter.class);
		system.eventStream().publish("msg",actor);
		
	}
	public static void main(String[] args) {
		DeadLetterApp app =new DeadLetterApp();
		app.subscribe();
		
	}
}
