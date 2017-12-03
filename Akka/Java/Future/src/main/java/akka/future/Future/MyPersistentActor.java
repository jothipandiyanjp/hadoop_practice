package akka.future.Future;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class MyPersistentActor extends UntypedActor{
	private LoggingAdapter log = Logging.getLogger(context().system(),this);

	
	@Override
	public void onReceive(Object message) throws Exception {
		log.debug(""+ message);
		if(message instanceof String){
//			Thread.sleep(5000);
			getSender().tell("hi", getSelf());
			
		}else{
			unhandled(message);
		}
			
	}
	
	
	
}
