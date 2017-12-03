package akka.future.Future;

import scala.concurrent.Future;
import scala.concurrent.Promise;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.dispatch.OnComplete;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.future.Future.UsingPipe.PrintCompletedResult;
import akka.future.Future.UsingPipe.PrintFailureResult;
import akka.future.Future.UsingPipe.PrintSuccessResult;
import akka.pattern.Patterns;
import akka.pattern.PipeToSupport.PipeableFuture;
import akka.util.Timeout;

public class FutuersExample {

	ActorSystem system = ActorSystem.create("akka");

	private LoggingAdapter log = Logging.getLogger(system, this);

	public final class PrintSuccessResult<T> extends OnSuccess<T> {
		@Override
		public final void onSuccess(T t) {
			log.debug("Onsuccess" + Thread.currentThread());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.error(e.getMessage());
			}
			log.debug("Future success " + t);
		}

	}

	public final class PrintFailureResult extends OnFailure {

		@Override
		public void onFailure(Throwable arg0) throws Throwable {

		}
	}

	public final class PrintCompletedResult extends OnComplete {

		@Override
		public void onComplete(Throwable failure, Object success) throws Throwable {
			log.debug("Oncomplete "+success);
		}

	}

	public void getMsgWithFuture() throws Exception {
		ActorRef ref = system.actorOf(Props.create(MyPersistentActor.class),
				"myPersistentActor");

=
		// Sending Hello to actor
		Future<String> future = Futures.future(() ->{ log.debug("Current thread "+Thread.currentThread());return "Hello" + "World";},
				system.dispatcher());

		Future<String> future1 = Futures.future(() -> "Hello" + "World",
				system.dispatcher());
		
		future.onSuccess(new PrintSuccessResult<String>(), system.dispatcher());
		future1.onSuccess(new PrintSuccessResult<String>(), system.dispatcher());
		log.debug("Method ends here...");

		Futures.successful("Msg succeeded");
		Futures.failed(new IllegalArgumentException("Msg failed"));

		/*
		 * Creating empty promise
		 */
		Promise<Object> promise = Futures.promise();
		Future<Object> theFuture = promise.future();
		theFuture.onComplete(new PrintCompletedResult(), system.dispatcher());
		
		promise.success("Promise success");

		/*
		 * once again calling promises methods ( success() or completed() or failure() )after  
		 * success() or completed() or failure() methods, it will throw "Promise already completed. exception"
		 */
		promise.failure(new IllegalArgumentException()); 
	}

	public static void main(String[] args) throws Exception {
		FutuersExample actors = new FutuersExample();
		actors.getMsgWithFuture();
	}

}
