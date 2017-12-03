package sample.remote.calculator;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class CalculatorActor extends UntypedActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
  public void onReceive(Object message) {
		log.debug("Message : "+message);
	}
  
}
