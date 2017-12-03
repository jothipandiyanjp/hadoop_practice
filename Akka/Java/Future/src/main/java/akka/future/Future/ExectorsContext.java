package akka.future.Future;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import scala.concurrent.ExecutionContext;
import scala.concurrent.ExecutionContextExecutorService;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.ExecutionContexts;
import akka.dispatch.Futures;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.util.Timeout;


public class ExectorsContext {
	ActorSystem system = ActorSystem.create("akka");

	private LoggingAdapter log = Logging.getLogger(system, this);

	public final class PrintSuccessResult<T> extends OnSuccess<T> {
		@Override
		public final void onSuccess(T t) {
			log.debug("OnSuccess "+Thread.currentThread());
			log.debug("Future success " + t);
		}

	}

	public void getMsgWithFuture() throws Exception {
		log.debug(""+Thread.currentThread());
		ExecutorService pool = Executors.newCachedThreadPool();
		ExecutionContext ec = ExecutionContexts.fromExecutorService(pool);
		ActorRef ref = system.actorOf(Props.create(MyPersistentActor.class),"myPersistentActor");
		Timeout timeout = new Timeout(Duration.create(5,"seconds"));

		Future<Future<Object>> future = Futures.successful(Patterns.ask(ref, "Hello..!", timeout));
		future.onSuccess(new PrintSuccessResult(), ec);

		Future<Integer> future1 = Futures.successful(2+1);
		future1.onSuccess(new PrintSuccessResult(), ec);

		pool.shutdown();
		log.debug("End of the method "+Thread.currentThread());
		
	}

	public static void main(String[] args) throws Exception {
		ExectorsContext actors = new ExectorsContext();
		actors.getMsgWithFuture();
	}
}
