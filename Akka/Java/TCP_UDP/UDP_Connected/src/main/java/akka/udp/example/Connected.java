package akka.udp.example;

import java.net.InetSocketAddress;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.UdpConnected;
import akka.io.UdpConnectedMessage;
import akka.japi.Procedure;
import akka.util.ByteString;

public class Connected extends UntypedActor {

	final InetSocketAddress remote;

	public static Props props(InetSocketAddress inet) {
		return Props.create(Connected.class, inet);
	}

	public Connected(InetSocketAddress remote) {
		final ActorRef mgr = UdpConnected.get(getContext().system())
				.getManager();
		this.remote = remote;
		mgr.tell(UdpConnectedMessage.connect(getSelf(), remote), getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof UdpConnected.Connected) {
			getContext().become(ready(getSender()));
		} else {
			unhandled(message);
		}
	}

	private Procedure<Object> ready(final ActorRef connection) {

		return new Procedure<Object>() {
			public void apply(Object message) throws Exception {
				if (message instanceof UdpConnected.Received) {

					// 2. Getting "hello" from listner and sending "world" to listner

					final UdpConnected.Received r = (UdpConnected.Received) message;
					if (r.data().utf8String().equals("hello")) {
						connection.tell(UdpConnectedMessage.send(ByteString
								.fromString("world")), getSelf());
					}

					/*
					 * 1. Getting msesage "hello" from main method and sending
					 * to String
					 */
				} else if (message instanceof String) {
					final String str = (String) message;
					connection.tell(UdpConnectedMessage.send(ByteString
							.fromString(str)), getSelf());
				} else if (message.equals(UdpConnectedMessage.disconnect())) {
					connection.tell(message, getSelf());
				} else if (message instanceof UdpConnected.Disconnected) {
					getContext().stop(getSelf());
				} else {
					unhandled(message);
				}

			}
		};

	}

}
