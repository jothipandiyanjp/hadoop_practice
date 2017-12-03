package akka.cluster.ddata.app;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.ddata.example.DataBot;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class DataBotApp {

	public void replicate(){
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+3552).withFallback(ConfigFactory.load());

		ActorSystem system = ActorSystem.create("ClusterSystem",config);

		ActorRef ref = system.actorOf(Props.create(DataBot.class),"clusterListner1");		
		
	}
	
	public static void main(String[] args) {
		DataBotApp app = new DataBotApp();
		app.replicate();
	}
}
