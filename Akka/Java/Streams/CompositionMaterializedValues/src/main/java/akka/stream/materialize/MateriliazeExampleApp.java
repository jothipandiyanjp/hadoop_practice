package akka.stream.materialize;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicLong;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.actor.ActorSubscriber;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class MateriliazeExampleApp {
	private final ActorSystem system = ActorSystem.create("stream-composition");
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	public Flow<Long, Long, Counter> counter() {
		AtomicLong internalCounter = new AtomicLong(0);
		return Flow.of(Long.class).map(elem -> {
			internalCounter.incrementAndGet();
			return elem;
		}).<Counter>mapMaterializedValue(i -> {
			return 	new Counter() {
				@Override
				public long get() {
					return internalCounter.get(); 
				}
			};
		});
	}

	public void materialization() {
		
		final Source<Long, NotUsed> source = Source.from(Arrays.asList(10l, 2l,
				3l, 4l, 5l));

		final Sink<Long, CompletionStage<Long>> sink = Sink.fold(Long.valueOf(0), (
				Long acc, Long i) -> acc + i);
		
		Pair<Counter, CompletionStage<Long>> result = source.viaMat(counter(),Keep.right())
														.toMat(sink, Keep.both()).run(materializer);

		log.debug("count {Total elements }-> "+result.first().get());
		result.second().whenComplete((i,j) -> log.debug("sum of n numbers -> "+i));
		
	}

	public static void main(String[] args) {
		MateriliazeExampleApp app = new MateriliazeExampleApp();
		app.materialization();
	}

}

interface Counter {
	public long get();
}