package akka.future.Future;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;

public class Futures_Traverse {
	ActorSystem system = ActorSystem.create("akka");

	private LoggingAdapter log = Logging.getLogger(system, this);

	public final class PrintResult<T> extends OnSuccess<T> {
		@Override
		public final void onSuccess(T upperCaseCharacters) {
			log.info("characters in uppercase");

			for(String s: (Iterable<String>)upperCaseCharacters)
				log.debug(s);

		}
	}

	private void getMsgWithFuture() {
		final ExecutionContext executor = system.dispatcher();
		Iterable<String> listStrings = Arrays.asList("a","b","c","d");
		Future<Iterable<String>> futureResult = Futures.traverse(listStrings, new Function<String, Future<String>>() {
			@Override
			public Future<String> apply(String ch) throws Exception {
				return Futures.future(new Callable<String>() {
					@Override
					public String call() throws Exception {
						return ch.toUpperCase();
					}
				}, executor);
			}
		}, executor);
		
		futureResult.onSuccess(new PrintResult<Iterable<String>>(), executor);
		
	}

	public static void main(String[] args) throws Exception {
		Futures_Traverse future = new Futures_Traverse();
		future.getMsgWithFuture();
	}
}
