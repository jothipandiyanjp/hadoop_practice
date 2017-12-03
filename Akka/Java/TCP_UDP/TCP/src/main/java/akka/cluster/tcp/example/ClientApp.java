package akka.cluster.tcp.example;

import java.net.InetSocketAddress;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.io.Tcp.Bound;

public class ClientApp {
	private final ActorSystem system = ActorSystem.create("TcpExample");
	private final LoggingAdapter log = Logging.getLogger(system, this);

	public void getTcpManager() {
		
				
		
		ActorRef server = system.actorOf(Server.props(Tcp.get(system).manager()));
		
		// connect client to server port (8081)
		InetSocketAddress inet = new InetSocketAddress("localhost",8081);

		ActorRef ref = system.actorOf(Props.create(Client.class,inet,server));

	}

	public static void main(String[] args) {
		ClientApp cl = new ClientApp();
		cl.getTcpManager();
	}
	
}
