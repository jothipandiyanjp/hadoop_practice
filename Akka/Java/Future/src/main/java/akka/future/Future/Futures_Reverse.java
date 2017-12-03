package akka.future.Future;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.dispatch.Recover;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import akka.japi.Function2;

public class Futures_Reverse {
	ActorSystem system = ActorSystem.create("akka");

	private LoggingAdapter log = Logging.getLogger(system, this);

	public final class PrintResult<T> extends OnSuccess<T> {
		@Override
		public final void onSuccess(T result) {
			log.info("result = "+result);
		}
	}
	public final class PrintResultFailure extends OnFailure{
		@Override
		public void onFailure(Throwable failure) throws Throwable {
			log.debug("failure occured .."+failure.getMessage());
		}
	}

	private void getMsgWithFuture() {
		final ExecutionContext executor = system.dispatcher();
		List<Future<Integer>> source = new ArrayList<Future<Integer>>();
		for (int i = 0; i < 10; i++) 
			source.add(Futures.successful(i));
		
		Iterable<Future<Integer>> flist = source; 
		
		Future<Integer> futureResult = Futures.reduce(flist, new Function2<Integer, Integer,Integer >() {
			@Override
			public Integer apply(Integer num1, Integer num2) throws Exception {	
//				int x =1/0;
				return num1+num2;
			}
		}, executor).recover(new Recover<Integer>() {
			@Override
			public Integer recover(Throwable failure) throws Throwable {
				if(failure instanceof ArithmeticException)
					return 0;				
				else
					throw failure;
			}
		}, executor);
		futureResult.onSuccess(new PrintResult<Integer>(), executor);
	}
	
	public static void main(String[] args) {
		Futures_Reverse ff = new Futures_Reverse();
		ff.getMsgWithFuture();
	}
}
