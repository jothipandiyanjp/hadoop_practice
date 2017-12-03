package akka.future.Future;

import scala.concurrent.Await;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.ExecutionContexts;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.util.Timeout;

public class WithActors {

	ActorSystem system = ActorSystem.create("akka");

	private LoggingAdapter log = Logging.getLogger(system, this);

	public void getMsgWithFuture() throws Exception {
		ActorRef ref = system.actorOf(Props.create(MyPersistentActor.class),"myPersistentActor");

		Timeout timeout = new Timeout(Duration.create(5, "seconds"));
		Future<Object> future = Patterns.ask(ref, "Hello..!", timeout);
		String result = (String) Await.result(future, timeout.duration()); // Block the thread till getting result

		Future<Object> future1 = Patterns.ask(ref, "Hello..!", timeout);
		String result1 = (String) Await.result(future1, timeout.duration());
		
		log.debug(result);
		log.debug(result1);

	}

	public static void main(String[] args) throws Exception {
		WithActors actors = new WithActors();
		actors.getMsgWithFuture();
	}
}
