package multi.source.sink.combining.example;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class MyActor extends UntypedActor{

	private LoggingAdapter log  = Logging.getLogger(getContext().system(), this);
	@Override
	public void onReceive(Object message) throws Exception {
		
		log.debug("Message received in MyActor -> "+message);
		
	}
}
