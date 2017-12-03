package akka.logging.example;

import scala.Option;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import  akka.event.slf4j.Slf4jLogger;

public class MyActor extends UntypedActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void preStart() throws Exception {
	    log.debug("Starting");
	}
	
	@Override
	public void preRestart(Throwable reason, Option<Object> message)
			throws Exception {
	    log.error(reason, "Restarting due to [{}] when processing [{}]",
	    	      reason.getMessage(), message.isDefined() ? message.get() : "");
	}
	@Override
	public void onReceive(Object message) throws Exception {

		if (message.equals("test")) {
		      log.info("Received test");
		    } else {
		      log.warning("Received unknown message: {}", message);
		    }
	}

}
