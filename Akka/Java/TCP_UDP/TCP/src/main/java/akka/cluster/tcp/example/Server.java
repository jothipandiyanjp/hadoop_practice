package akka.cluster.tcp.example;

import java.net.InetSocketAddress;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.io.Tcp.Bound;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.io.TcpMessage;

public class Server extends UntypedActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	final ActorRef manager;

	public Server(ActorRef manager) {
		this.manager = manager;
	}

	public static Props props(ActorRef manager) {
		return Props.create(Server.class, manager);
	}

	@Override
	public void preStart() throws Exception {
		final ActorRef tcp = Tcp.get(getContext().system()).manager();
		tcp.tell(TcpMessage.bind(getSelf(), new InetSocketAddress("localhost", 8081), 100), getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Bound) {
			manager.tell(message, getSelf());
		} else if (message instanceof CommandFailed) {
			getContext().stop(getSelf());
		} else if (message instanceof Connected) {
			log.debug("connected server");
			final Connected conn = (Connected) message;
			manager.tell(conn, getSelf());

			final ActorRef handler = getContext().actorOf(Props.create(SimplisticHandler.class));
			getSender().tell(TcpMessage.register(handler), getSelf());

		}
	}
}