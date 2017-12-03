package akka.cluster.stats.app;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.stats.actors.StatsService;
import akka.cluster.stats.actors.StatsWorker;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class StatsSampleMain {
	public static void main(String[] args) {
	    if (args.length == 0) {
	      startup(new String[] { "2551","2552","0"});
	      StatsSampleClientMain.main(new String[0]);
	    }
	  }
	
	public static void startup(String[] ports) {
	    for (String port : ports) {
	    	  Config config = ConfigFactory
	    	          .parseString("akka.remote.netty.tcp.port=" + port)
	    	          .withFallback(
	    	              ConfigFactory.parseString("akka.cluster.roles = [compute]"))
	    	          .withFallback(ConfigFactory.load("stats1"));
	    	 
	    	  ActorSystem system = ActorSystem.create("ClusterSystem",config);

	        system.actorOf(Props.create(StatsWorker.class), "statsWorker");

	        system.actorOf(Props.create(StatsService.class), "statsService");

	    }
	}    
}
