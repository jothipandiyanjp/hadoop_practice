package akka.become.example.Become_Unbecome;

import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;

import static akka.become.example.Become_Unbecome.Messages.*;

public class Chopstick extends AbstractActor{

	PartialFunction<Object, BoxedUnit> takenBy(ActorRef hacker){
		return ReceiveBuilder.match(Take.class, t -> t.hakker.tell(new Busy(self()), self()))
				 			.match(Put.class, p -> p.hakker == hacker, p -> context().become(available) ).build();
	}

	PartialFunction<Object, BoxedUnit> available = ReceiveBuilder.match(Take.class, t -> {
        context().become(takenBy(t.hakker));
        t.hakker.tell(new Taken(self()), self());
	}).build();

    public Chopstick() {
        receive(available);
      }
}


