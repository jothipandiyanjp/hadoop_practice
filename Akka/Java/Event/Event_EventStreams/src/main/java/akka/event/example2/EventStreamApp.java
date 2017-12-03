package akka.event.example2;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.DeadLetter;
import akka.actor.Props;
import akka.event.Logging;

public class EventStreamApp {

	public void publish(){
		final ActorSystem system = ActorSystem.create("EventStream");
		final ActorRef actor = system.actorOf(Props.create(DeadLetterActor.class));
		  
	    system.eventStream().subscribe(actor, DeadLetter.class);
	    final ActorRef jazzListener = system.actorOf(Props.create(Listener.class));
	    final ActorRef musicListener = system.actorOf(Props.create(Listener.class));

	    // Listens to Jazz music only
	    system.eventStream().subscribe(jazzListener, Jazz.class);
	    
	    //Listens to all kind of music ( jazz and music listeners  )
	    system.eventStream().subscribe(musicListener, AllKindsOfMusic.class);
	   
	    // only musicListener gets message, since it listens to "allKindOfMusic"  
	    system.eventStream().publish(new Electronic("Parov Stelar"));

	    // jazzListener and musicListener will be notified about Jazz:
	    system.eventStream().publish(new Jazz("Sonny Rollins"));
	}
	
	public static void main(String[] args) {
		EventStreamApp app =new EventStreamApp();
		app.publish();
	}
}
