package akka.cluster.seednode;

import java.util.Arrays;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.example.SimpleClusterApp;
import akka.cluster.example.SimpleClusterListener;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class JoiningSeedNodeProgrammatically {

	public void startCluster(){
		
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+ 2556).withFallback(ConfigFactory.load());

//		Config config = ConfigFactory.parseString("akka.actor.provider=akka.cluster.ClusterActorRefProvider").withFallback(ConfigFactory.load());
		ActorSystem system = ActorSystem.create("ClusterSystem",config);
		
		Cluster.get(system).joinSeedNodes(Arrays.asList(new Address("akka.tcp", "ClusterSystem","192.168.1.11", 2551)));
		
		ActorRef ref = system.actorOf(Props.create(SimpleClusterListener.class),"clusterListner");		
		ref.tell("From SimpleClusterApp",ActorRef.noSender());
		
	}
	
	public static void main(String[] args) {
		JoiningSeedNodeProgrammatically app =new JoiningSeedNodeProgrammatically();
		app.startCluster();
	}

}
