package akka.udp.example;

import java.net.InetSocketAddress;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class UDPApp {
	private final ActorSystem system = ActorSystem.create("UDPExample");
	private final LoggingAdapter log = Logging.getLogger(system, this);

	public void getTcpManager() {
		
		// Preparing sender 
		InetSocketAddress remote = new InetSocketAddress("localhost",8081);				
		ActorRef sender = system.actorOf(Connected.props(remote),"sender");
		
		// Preparing Listener to the port 
		ActorRef ref = system.actorOf(Props.create(Listener.class,sender),"listner");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		sender.tell("hello", null);
		
	}

	public static void main(String[] args) {
		UDPApp cl = new UDPApp();
		cl.getTcpManager();
	}
	
}
