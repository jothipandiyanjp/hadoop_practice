package akka.cluster.pubsub.app;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.pubsub.Publisher;

public class PusblisherMain {
	
	public void createTopic(){
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+ 2553).withFallback(ConfigFactory.load());

		ActorSystem system = ActorSystem.create("pubsub",config);

//		Address joinAddress = Cluster.get(system).selfAddress();
	//	Cluster.get(system).join(joinAddress);
	
		ActorRef publisher = system.actorOf(Props.create(Publisher.class),"publisher");
	       try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for(int i=0;i<1000000;i++)
			publisher.tell("hello", null);

	}
	
	public static void main(String[] args) {
		PusblisherMain main = new PusblisherMain();
		main.createTopic();
	}
}
