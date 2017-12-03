package akka.stream.example;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.omg.PortableInterceptor.INACTIVE;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.ClosedShape;
import akka.stream.Materializer;
import akka.stream.Outlet;
import akka.stream.UniformFanInShape;
import akka.stream.UniformFanOutShape;
import akka.stream.javadsl.Broadcast;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.Merge;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class BroadCastMergingFlowsUsingGraphApp {

	private final ActorSystem system = ActorSystem.create("graph-dsl");
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	public void graphDSLModel() {

		final Source<Integer, NotUsed> in = Source.from(Arrays.asList(1, 2, 3,
				4, 5));
		final Sink<List<String>, CompletionStage<List<String>>> sink = Sink.head();

		final Flow<Integer, Integer, NotUsed> f1 = Flow.of(Integer.class).map(
				element -> element + 10);
		final Flow<Integer, Integer, NotUsed> f2 = Flow.of(Integer.class).map(
				element -> element + 20);
		final Flow<Integer, String, NotUsed> f3 = Flow.of(Integer.class).map(
				element -> element.toString());
		final Flow<Integer, Integer, NotUsed> f4 = Flow.of(Integer.class).map(
				element -> element + 30);

		final RunnableGraph<CompletionStage<List<String>>> result = RunnableGraph
				.<CompletionStage<List<String>>> fromGraph(GraphDSL
						.create(sink,
								(builder, out) -> {
									final UniformFanOutShape<Integer, Integer> bcast = builder
											.add(Broadcast.create(2));
									final UniformFanInShape<Integer, Integer> merge = builder
											.add(Merge.create(2));

									final Outlet<Integer> source = builder.add(
											in).out();
									builder.from(source).via(builder.add(f1))
											.viaFanOut(bcast)
											.via(builder.add(f2))
											.viaFanIn(merge)
											.via(builder.add(f3.grouped(1000)))
											.to(out);
									builder.from(bcast).via(builder.add(f4))
											.toFanIn(merge);

									return ClosedShape.getInstance();
								}));
// 1. Get result as List
	    try {
			final List<String> list = result.run(materializer).toCompletableFuture().get(3, TimeUnit.SECONDS);
			log.debug(""+list);
	    } catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}

//  2. Get result using whenComplete()
	    result.run(materializer).whenComplete((results, failure) -> {
			results.stream().forEach(p -> log.debug(p));
		});
		
	}

	public static void main(String[] args) {
		BroadCastMergingFlowsUsingGraphApp app = new BroadCastMergingFlowsUsingGraphApp();
		app.graphDSLModel();
	}
}
