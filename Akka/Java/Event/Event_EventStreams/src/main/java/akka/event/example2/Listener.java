package akka.event.example2;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Listener extends UntypedActor {
	private final LoggingAdapter log =Logging.getLogger(getContext().system(), this);
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Jazz) {
			log.debug(self().path().name()+" is listening to: "+message);
		} else if (message instanceof Electronic) {
			log.debug(self().path().name()+" is listening to: "+message);
		}
	}
}