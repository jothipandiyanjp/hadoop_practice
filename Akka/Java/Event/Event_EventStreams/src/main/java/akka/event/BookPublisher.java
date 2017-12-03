package akka.event;

import akka.actor.UntypedActor;

public class BookPublisher extends UntypedActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof Book){
			log.debug("Publishing new book "+message);
			context().system().eventStream().publish(message);
		}
	}
}
