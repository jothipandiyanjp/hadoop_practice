package akka.stream.example;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Framing;
import akka.stream.javadsl.FramingTruncation;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Sink;
import akka.util.ByteString;

public class ReduceByKeyApp {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	public void digestExample() {
		final Source<String, NotUsed> words = Source.from(Arrays.asList(
				"hello", "world", "and", "hello", "akka"));
		final int MAXIMUM_DISTINCT_WORDS = 1000;

		final Source<Pair<String, Integer>, NotUsed> counts = words
				.groupBy(MAXIMUM_DISTINCT_WORDS, i -> i) // split the words into
															// separate streams
															// first

				.map(i -> new Pair<>(i, 1)) // transform each element to pair
											// with number of words in it
				
				.reduce((left, right) -> new Pair<>(left.first(), left.second()
						+ right.second())) // add counting logic to the streams
				
						.mergeSubstreams(); // get a stream of word counts

		final CompletionStage<List<Pair<String, Integer>>> f = counts.grouped(
				10).runWith(Sink.head(), materializer);

		try {
			final Set<Pair<String, Integer>> result = f.toCompletableFuture()
					.get(3, TimeUnit.SECONDS).stream()
					.collect(Collectors.toSet());

			log.debug(""+result);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		ReduceByKeyApp example = new ReduceByKeyApp();
		example.digestExample();
	}
}
