package akka.cluster.pubsub.app;


import com.typesafe.config.Config;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.pubsub.Subscriber;
  
public class SubscriberMain {
	public void subscribeToTopic(){
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+ 2554).withFallback(ConfigFactory.load());	

		ActorSystem system = ActorSystem.create("pubsub",config);

	//	Address joinAddress = Cluster.get(system).selfAddress();
	//	 Cluster.get(system).join(joinAddress);
	    
		ActorRef subscriber = system.actorOf(Props.create(Subscriber.class),"subscriber2");
		
	}	
	public static void main(String[] args) {
		SubscriberMain main = new SubscriberMain();
		main.subscribeToTopic();
	}
}
