package akka.example;

import akka.actor.UntypedActor;

public class MyDestination extends UntypedActor {

	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Msg) {
		      Msg msg = (Msg) message;
		      getSender().tell(new Confirm(msg.deliveryId), getSelf());
		}else
			unhandled(message);
	}
}
