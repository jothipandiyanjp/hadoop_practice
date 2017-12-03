package akka.cluster.stats.app;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.stats.actors.StatsSampleClient;

public class StatsSampleClientMain {
	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("ClusterSystem", ConfigFactory.load("stats1"));

		system.actorOf(Props.create(StatsSampleClient.class, "/user/statsService"),"client");
		
	}
}
