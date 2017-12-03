package akka.event;

import akka.actor.UntypedActor;

public class BookSubscriber extends UntypedActor{

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void preStart() throws Exception {
		getContext().system().eventStream().subscribe(getSelf(), Book.class);
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof Book){
			log.debug("Subscriber -> "+getSelf().path().name() +" Reacived a new book : "+message);
		}else{
			unhandled(message);
		}
	}
}
