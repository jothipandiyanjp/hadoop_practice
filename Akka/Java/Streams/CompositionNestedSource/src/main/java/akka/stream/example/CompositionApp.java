package akka.stream.example;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.function.Procedure;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class CompositionApp {

	private final ActorSystem system = ActorSystem.create("stream-composition");
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	public void nestedFlow() {
		final Source<Integer, NotUsed> nestedSource = 
				Source.<Integer> single(0)
				    	.map(i -> i + 5) // i =5
				    	.named("nestedSource");

		final Flow<Integer, Integer, NotUsed> nestedFlow = 
				Flow.of(Integer.class)
					.filter(i -> i > 0)
					.map(i -> i - 2) // i = 3
					.named("nestedFLow");

		final Sink<Integer, CompletionStage<Integer>> nestedSink = 
				Sink.fold(5,(Integer acc, Integer i) -> acc + i); // i= 8

		nestedSource.viaMat(nestedFlow, Keep.left())
				.runWith(nestedSink, materializer)
				.whenComplete((x, y) -> log.debug("" + x));
	}

	public static void main(String[] args) {
		CompositionApp app = new CompositionApp();
		app.nestedFlow();
	}

}
