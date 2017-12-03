package akka.cluster.example;


import akka.actor.ActorRef;

import akka.actor.ActorSystem;
import akka.actor.Props;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class SimpleClusterApp {
	
	public void startCluster(){
		
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+ 2552).withFallback(ConfigFactory.load());

		ActorSystem system = ActorSystem.create("ClusterSystem",config);

		ActorRef ref = system.actorOf(Props.create(SimpleClusterListener.class),"clusterListner");		
		
		//ref.tell("From SimpleClusterApp",ActorRef.noSender());		

	}
	
	public static void main(String[] args) {
		SimpleClusterApp app =new SimpleClusterApp();
		app.startCluster();
	}
}
