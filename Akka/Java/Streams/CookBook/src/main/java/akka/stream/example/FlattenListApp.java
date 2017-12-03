package akka.stream.example;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import scala.Function1;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.function.Procedure;
import akka.stream.ActorMaterializer;
import akka.stream.Attributes;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Sink;

public class FlattenListApp {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	
	public void usingMapConcat(){
		Source<List<Integer>, NotUsed> someDataSource = Source
		          .from(Arrays.asList(Arrays.asList(1), Arrays.asList(2, 3)));

		// flatten 	List<Integer> to Integer
        Source<Integer, NotUsed> flattened = someDataSource.mapConcat(i -> i);
        List<Integer> got;
		try {
			got = flattened.limit(10).runWith(Sink.seq(), materializer).toCompletableFuture().get(1, TimeUnit
					.SECONDS);
	        log.debug(""+got);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		FlattenListApp example = new FlattenListApp();
		example.usingMapConcat();;
	}
}
