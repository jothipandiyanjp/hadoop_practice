package akka.udp.example;

import java.net.InetSocketAddress;


import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Udp;
import akka.io.UdpMessage;
import akka.japi.Procedure;
import akka.util.ByteString;

public class SimpleSender extends UntypedActor {

	final InetSocketAddress remote;
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public SimpleSender(InetSocketAddress remote) {
		this.remote = remote;
		final ActorRef mgr = Udp.get(getContext().system()).getManager();
		mgr.tell(UdpMessage.simpleSender(), getSelf());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static Props props(InetSocketAddress inet) {
		return Props.create(SimpleSender.class, inet);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Udp.SimpleSenderReady) {
			getContext().become(ready(getSender()));
		} else
			unhandled(message);
	}

	private Procedure<Object> ready(final ActorRef send) {
		return new Procedure<Object>() {
			public void apply(Object message) throws Exception {
				if (message instanceof String) {
					final String str = (String) message;
					// Sending message to Remote
					send.tell(
							UdpMessage.send(ByteString.fromString(str), remote),
							getSelf());
				} else {
					unhandled(message);
				}
			}
		};
	}
}
