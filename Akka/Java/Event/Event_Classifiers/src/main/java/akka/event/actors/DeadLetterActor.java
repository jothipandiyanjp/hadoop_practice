package akka.event.actors;

import akka.actor.DeadLetter;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class DeadLetterActor extends UntypedActor{

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void onReceive(Object message) throws Exception {
	    if (message instanceof DeadLetter) {
	    	log.debug("Deadletters -> "+message);
	    }else{
	    	unhandled(message);
	    }
	}
}
