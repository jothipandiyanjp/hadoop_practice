package akka.cluster.ddata.app;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.ddata.example.DataBot;
import akka.cluster.ddata.example.Replicator_Update;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Replicator_UpdateApp {

	public void replicate(){
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+3551).withFallback(ConfigFactory.load());

		ActorSystem system = ActorSystem.create("ClusterSystem",config);

		ActorRef ref = system.actorOf(Props.create(Replicator_Update.class),"clusterListner");		
		
	}
	
	public static void main(String[] args) {
		Replicator_UpdateApp app = new Replicator_UpdateApp();
		app.replicate();
	}
}
