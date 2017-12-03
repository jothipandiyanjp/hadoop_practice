package akka.future.Future;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import akka.japi.Function2;

public class Futures_Fold {
	ActorSystem system = ActorSystem.create("akka");

	private LoggingAdapter log = Logging.getLogger(system, this);

	public final class PrintResult<T> extends OnSuccess<T> {
		@Override
		public final void onSuccess(T result) {
			log.info("result = "+result);

		}
	}

	private void getMsgWithFuture() {
		final ExecutionContext executor = system.dispatcher();
		List<Future<Integer>> source = new ArrayList<Future<Integer>>();
		for (int i = 0; i < 2; i++) 
			source.add(Futures.successful(i));
		
		Iterable<Future<Integer>> flist = source; 

		Future<Integer> futureResult = Futures.fold(0,flist, new Function2<Integer, Integer,Integer >() {
			@Override
			public Integer apply(Integer num1, Integer num2) throws Exception {
				return num1+num2;
			}
		}, executor);
		futureResult.onSuccess(new PrintResult<Integer>(), executor);
	}
	public static void main(String[] args) {
		Futures_Fold ff = new Futures_Fold();
		ff.getMsgWithFuture();
	}
}
