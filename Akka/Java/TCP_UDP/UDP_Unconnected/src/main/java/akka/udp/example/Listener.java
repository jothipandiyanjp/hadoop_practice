package akka.udp.example;

import java.net.InetSocketAddress;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Udp;
import akka.io.UdpMessage;
import akka.japi.Procedure;
import akka.util.ByteString;

public class Listener extends UntypedActor {
	final ActorRef nextActor;
	private final LoggingAdapter log = Logging.getLogger(getContext().system(),
			this);

	public Listener(ActorRef nextActor) {
		this.nextActor = nextActor;

		final ActorRef mgr = Udp.get(getContext().system()).getManager();

		mgr.tell(UdpMessage.bind(getSelf(), new InetSocketAddress("localhost",8081)), getSelf());

	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Udp.Bound) {
			log.debug("LIstner bounded " + message);
			final Udp.Bound b = (Udp.Bound) message;
			getContext().become(ready(getSender()));
		} else {
			unhandled(message);
		}
	}

	private Procedure<Object> ready(final ActorRef socket) {
		return new Procedure<Object>() {
			public void apply(Object message) throws Exception {
				if (message instanceof Udp.Received) {
					final Udp.Received r = (Udp.Received) message;

					socket.tell(UdpMessage.send(r.data(), r.sender()),getSelf());

					// Getting bytestring from Udp.Received
					final ByteString processed = r.data();

					// decoding bytestring to string
					log.debug(processed.decodeString("UTF-8"));

					nextActor.tell(processed, getSelf());
					
				}
			}
		};
	}
}
