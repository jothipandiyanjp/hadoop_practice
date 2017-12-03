package akka.stream.subscriber.example;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Reply extends UntypedActor{
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	
	@Override
	public void onReceive(Object msg) throws Exception {
		log.debug(""+msg);
	}
}
