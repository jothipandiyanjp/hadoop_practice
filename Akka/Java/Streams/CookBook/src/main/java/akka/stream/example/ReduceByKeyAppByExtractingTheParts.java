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
import akka.japi.function.Function;
import akka.japi.function.Function2;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Framing;
import akka.stream.javadsl.FramingTruncation;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Sink;
import akka.util.ByteString;

public class ReduceByKeyAppByExtractingTheParts {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	
	
	  static public <In, K, Out> Flow<In, Pair<K, Out>, NotUsed> reduceByKey(
			   int maximumGroupSize,
			      Function<In, K> groupKey,
			      Function<In, Out> map,
			      Function2<Out, Out, Out> reduce){
		  return Flow.<In> create()
			      .groupBy(maximumGroupSize, groupKey)
			      .map(i -> new Pair<>(groupKey.apply(i), map.apply(i)))
			      .reduce((left, right) -> new Pair<>(left.first(), reduce.apply(left.second(), right.second())))
			      .mergeSubstreams();
	  }

	public void digestExample() {
        final Source<String, NotUsed> words = Source.from(Arrays.asList("hello", "world", "and", "hello", "akka"));
        final int MAXIMUM_DISTINCT_WORDS = 1000;
        
        Source<Pair<String, Integer>, NotUsed> counts = words.via(reduceByKey(
                MAXIMUM_DISTINCT_WORDS,
                word -> word,
                word -> 1,
                (left, right) -> left + right));

		try {        
			final CompletionStage<List<Pair<String, Integer>>> f = counts.grouped(10).runWith(Sink.head(), materializer);

			final Set<Pair<String, Integer>> result = f.toCompletableFuture()
					.get(3, TimeUnit.SECONDS).stream()
					.collect(Collectors.toSet());

			log.debug(""+result);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		ReduceByKeyAppByExtractingTheParts example = new ReduceByKeyAppByExtractingTheParts();
		example.digestExample();
	}
}
