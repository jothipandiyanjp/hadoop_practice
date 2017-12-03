package akka.transform.app;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.transform.message.TransFormationBackend;
import akka.transform.message.TransformationFrontEnd;
import akka.transform.message.TransformationMessages;
import akka.transform.message.TransformationMessages.TransformationJob;
import akka.util.Timeout;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TransformationFrontendApp {
	
	private String port = "0";
	private final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+port)
												.withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]"))
												.withFallback(ConfigFactory.load());

	private final ActorSystem system = ActorSystem.create("ClusterSystem",config);
	private final LoggingAdapter log = Logging.getLogger(system, this);
	
	private void getActorSystem() {
		final ActorRef frontend = system.actorOf(Props.create(TransformationFrontEnd.class),"frontend");
		final ExecutionContext ec = system.dispatcher();
		FiniteDuration interval = Duration.create(2,TimeUnit.SECONDS);
	   final AtomicInteger counter = new AtomicInteger();
	    final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
		system.scheduler().schedule(interval, interval, 
				new Runnable() {
					
					public void run() {
						Patterns.ask(frontend, new TransformationJob("hello-"+counter.incrementAndGet()), timeout)
						.onSuccess(new OnSuccess<Object>() {
							@Override
							public void onSuccess(Object message) throws Throwable {
								log.info("OnSuccess -> "+((TransformationJob)message).getText());
							}
							
						}, ec);
					}
				}, ec);
		
	}
	public static void main(String[] args) {
		TransformationFrontendApp backendapp = new TransformationFrontendApp();
		backendapp.getActorSystem();
	}


}
