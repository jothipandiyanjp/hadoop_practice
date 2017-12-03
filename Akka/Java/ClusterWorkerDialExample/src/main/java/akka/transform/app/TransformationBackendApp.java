package akka.transform.app;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.transform.message.TransFormationBackend;

public class TransformationBackendApp {
	private String port = "0";

//		private final LoggingAdapter log = Logging.getLogger(system, this);

	private void getActorSystem() {
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+port)
				.withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
				.withFallback(ConfigFactory.load());

		ActorSystem system = ActorSystem.create("ClusterSystem",config);

		system.actorOf(Props.create(TransFormationBackend.class),"backend");
		
	}
	public static void main(String[] args) {
		TransformationBackendApp backendapp = new TransformationBackendApp();
		backendapp.getActorSystem();
	}

	
}
