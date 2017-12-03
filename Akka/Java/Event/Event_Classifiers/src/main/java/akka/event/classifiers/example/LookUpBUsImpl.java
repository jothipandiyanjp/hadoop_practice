package akka.event.classifiers.example;

import akka.actor.ActorRef;
import akka.event.japi.LookupEventBus;

public class LookUpBUsImpl extends
		LookupEventBus<MsgEnvelope, ActorRef, String> {

	@Override
	public String classify(MsgEnvelope event) {
		return event.topic;
	}

	@Override
	public void publish(MsgEnvelope event, ActorRef subscriber) {
		subscriber.tell(event.payload, ActorRef.noSender());
	}

	@Override
	public int compareSubscribers(ActorRef a, ActorRef b) {
		return a.compareTo(b);
	}

	@Override
	public int mapSize() {
		return 128;
	}
}
