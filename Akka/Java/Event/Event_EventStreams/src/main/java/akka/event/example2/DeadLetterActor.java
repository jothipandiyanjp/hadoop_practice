package akka.event.example2;

import akka.actor.DeadLetter;
import akka.actor.UntypedActor;

public class DeadLetterActor extends UntypedActor {
	public void onReceive(Object message) {
		if (message instanceof DeadLetter) {
			System.out.println(message);
		}
	}
}