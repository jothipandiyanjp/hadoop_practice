package schema.add.fields;



import akka.actor.ActorRef;

import akka.actor.ActorSystem;
import akka.actor.Props;
import schema.add.sample.SeatReserved;;

public class PersistentActorExample {
	public static void main(String[] args) {
		PersistentActorExample ex = new PersistentActorExample();
		ex.createEvents();
	}
	
	private void createEvents() {
		final ActorSystem system = ActorSystem.create("akka1");
		final ActorRef persistentActor = system.actorOf(Props.create(ExamplePersistenceActor.class),"persistentActor");
		persistentActor.tell(new SeatReserved("W",1,SeatType.Window), null);
		
		
	
	}
	
}
