package akka.future.Future;

import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Future_Map {
	ActorSystem system = ActorSystem.create("akka");

	private LoggingAdapter log = Logging.getLogger(system, this);

	public final class PrintResult<T> extends OnSuccess<T> {
		@Override
		public final void onSuccess(T length) {
		//	log.debug("Thread in onSuccess()" + Thread.currentThread());
			log.info("Length = " + length);
		}
	}

	private void getMsgWithFuture() {
		/*
		 * Default Dispatcher
		 */
//		 final ExecutionContext ec = system.dispatcher();
		 
		/*
		 * Our own dispatcher
		 */

//		final ExecutionContext ec = system.dispatchers().lookup("my-dispatcher");
		final ExecutionContext ec = system.dispatchers().lookup("my-thread-pool-dispatcher");

		log.debug("main thread :" + Thread.currentThread());
		Future<String> future1 = Futures.future(() -> "Hello" + "World", ec);

		for (int i = 0; i < 100; i++) {
			Future<Integer> future2 = future1.map(new Mapper<String, Integer>() {
						public Integer apply(String message) {
		//					log.debug("Thread in apply()"+ Thread.currentThread());
							return message.length();
						};
					}, ec);

			future2.onSuccess(new PrintResult<Integer>(), ec);
		}
		log.debug("Main Thread ends here");
	}

	public static void main(String[] args) throws Exception {
		Future_Map future = new Future_Map();
		future.getMsgWithFuture();
	}

}
