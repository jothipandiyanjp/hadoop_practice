package akka.future.Future;

import java.util.ArrayList;
import java.util.List;

import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Futures_Sequence {
	ActorSystem system = ActorSystem.create("akka");

	private LoggingAdapter log = Logging.getLogger(system, this);

	public final class PrintResult<T> extends OnSuccess<T> {
		@Override
		public final void onSuccess(T sum) {
			log.info("sum = " + sum);
		}
	}

	private void getMsgWithFuture() {
		final ExecutionContext executor = system.dispatcher();

		List<Future<Integer>> source = new ArrayList<Future<Integer>>();
		for (int i = 0; i < 10; i++) 
		source.add(Futures.successful(i));
		
		Future<Iterable<Integer>> futuerListOfInts = Futures.sequence(source, executor);
		
		Future<Long> futureSum = futuerListOfInts.map(new Mapper<Iterable<Integer>, Long>() {
			@Override
			public Long apply(Iterable<Integer> num) {
				long sum =0;
				for(Integer i:num)
					sum += i;
				return sum;
			}
		}, executor);
		futureSum.onSuccess(new PrintResult<Long>(), executor);
	}

	public static void main(String[] args) throws Exception {
		Futures_Sequence future = new Futures_Sequence();
		future.getMsgWithFuture();
	}
}
