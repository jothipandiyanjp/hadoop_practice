package akka.future.Future;

import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnComplete;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.pattern.PipeToSupport.PipeableFuture;
import akka.util.Timeout;

public class UsingPipe {
	ActorSystem system = ActorSystem.create("akka");

	private LoggingAdapter log = Logging.getLogger(system, this);

	public final class PrintSuccessResult<T> extends OnSuccess<T> {
		@Override
		public final void onSuccess(T t) {
			log.debug("Future success " + t);
		}

	}

	public final class PrintFailureResult extends OnFailure {
		@Override
		public void onFailure(Throwable arg0) throws Throwable {
		}
	}

	public final  class PrintCompletedResult<T> extends OnComplete<T> {
		@Override
		public void onComplete(Throwable failure, T success)
				throws Throwable {
		}
	}

	public void getMsgWithFuture() throws Exception {
		ActorRef ref = system.actorOf(Props.create(MyPersistentActor.class),
				"myPersistentActor");
		Timeout timeout = new Timeout(Duration.create(5, "seconds"));
		// Sending Hello to actor
		Future<Object> future = Patterns.ask(ref, "Hello..!", timeout);

		// Result from future, sending result to same actor or different actor
		PipeableFuture<Object> pipe = Patterns.pipe(future, system.dispatcher()).to(ref);

		future.onSuccess(new PrintSuccessResult<Object>(), system.dispatcher());
		future.onFailure(new PrintFailureResult(), system.dispatcher());
		future.onComplete(new PrintCompletedResult(), system.dispatcher());
		
	}

	public static void main(String[] args) throws Exception {
		UsingPipe actors = new UsingPipe();
		actors.getMsgWithFuture();
	}
}
