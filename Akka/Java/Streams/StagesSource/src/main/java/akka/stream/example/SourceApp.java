package akka.stream.example;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.*;
import akka.japi.function.Creator;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.SourceQueueWithComplete;

public class SourceApp {

	private final ActorSystem system = ActorSystem.create("akka-stream");
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	public void zipWithN() {
		final Source<Integer, NotUsed> source1 = Source.from(Arrays
				.asList(0, 1));
		final Source<Integer, NotUsed> source2 = Source.from(Arrays
				.asList(2, 3));
		final List<Source<Integer, ?>> sources = Arrays
				.asList(source1, source2);
		
	    final Source<Object, NotUsed> source = Source.zipWithN(list -> list, sources);

		source.runWith(Sink.foreach(i -> log.debug("" + i)), materializer);

	}

	public void zipN() {
		final Source<Integer, NotUsed> source1 = Source.from(Arrays
				.asList(0, 1));
		final Source<Integer, NotUsed> source2 = Source.from(Arrays
				.asList(2, 3));
		final List<Source<Integer, ?>> sources = Arrays
				.asList(source1, source2);
		final Source<List<Integer>, ?> source = Source.zipN(sources);

		source.runWith(Sink.foreach(i -> log.debug("" + i)), materializer);

	}

	public void queue() {
		final Pair<SourceQueueWithComplete<String>, CompletionStage<List<String>>> x = Flow
				.of(String.class).runWith(
						// buffer size and strategy
						Source.queue(5, OverflowStrategy.dropHead()),
						Sink.seq(), materializer);

		final SourceQueueWithComplete<String> source = x.first();
		final CompletionStage<List<String>> result = x.second();
		// send messsage with source.offer
		source.offer("hello");
		source.offer("world");
		source.complete();

		List<String> list;
		try {
			list = result.toCompletableFuture().get(3, TimeUnit.SECONDS);
			log.debug("" + list);

		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
	}

	public void unFold() {

		Source.unfold(5, i -> {
			if (i-- > 0)
				return Optional.of(Pair.apply(i, i));
			else
				return Optional.empty();
		}).runWith(Sink.foreach(i -> log.debug("" + i)), materializer);
	}

	public void fromCompletionStage() {
		final Iterable<String> input = Arrays.asList("A", "B", "C");
		CompletionStage<String> future1 = Source.from(input).runWith(
				Sink.<String> head(), materializer);

		Source.fromCompletionStage(future1).runWith(
				Sink.foreach(i -> log.debug("" + i)), materializer);

	}

	public void cycle() {
		// call the creator continuosly
		Creator<Iterator<Integer>> creator = new Creator<Iterator<Integer>>() {

			public Iterator<Integer> create() throws Exception {
				Iterable<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
				return input.iterator();
			}
		};

		Source.cycle(creator).runWith(Sink.foreach(i -> log.debug("" + i)),
				materializer);
	}

	public void repeat() {

		Source.repeat(5).take(5)
				.runWith(Sink.foreach(i -> log.debug("" + i)), materializer);
	}

	public void fromIterator() {
		Creator<Iterator<Integer>> creator = new Creator<Iterator<Integer>>() {

			public Iterator<Integer> create() throws Exception {
				Iterable<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
				return input.iterator();
			}
		};

		Source.fromIterator(creator).runWith(
				Sink.foreach(i -> log.debug("" + i)), materializer);
	}

	public void sample() {
		// fromIterator();
		// repeat();
		// cycle();
		// fromCompletionStage();
		// unFold();
		// queue();
		// zipN();
		   zipWithN();
	}

	public static void main(String[] args) {
		SourceApp app = new SourceApp();
		app.sample();
	}
}
