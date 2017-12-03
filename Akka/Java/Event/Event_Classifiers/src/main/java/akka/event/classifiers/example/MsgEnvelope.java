package akka.event.classifiers.example;

public class MsgEnvelope {

	public final String topic;
	public final Object payload;

	public MsgEnvelope(String topic, Object payload) {
		super();
		this.topic = topic;
		this.payload = payload;
	}

}
