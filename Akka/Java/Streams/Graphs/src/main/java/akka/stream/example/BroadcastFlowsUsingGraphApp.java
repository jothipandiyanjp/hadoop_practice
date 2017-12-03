package akka.stream.example;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.ClosedShape;
import akka.stream.Graph;
import akka.stream.Materializer;
import akka.stream.UniformFanOutShape;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.GraphDSL.Builder;
import akka.stream.javadsl.Broadcast;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class BroadcastFlowsUsingGraphApp {

	private final ActorSystem system = ActorSystem.create("graph-dsl");
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	private  Consumer<Object> printWithExampleInfo(String exampleInfo) {
		return p -> log.debug(exampleInfo + " " + p);
	}
	public void graphDSLModel() {

// 1. Getting result using accept		
		final Sink<Integer, CompletionStage<Done>> topHeadSink = Sink
				.foreach(e -> printWithExampleInfo("topHeadSink").accept(e));
		
// 		final Sink<Integer, CompletionStage<Integer>> bottomHeadSink = Sink.head();	 

		final Sink<Integer, CompletionStage<Integer>> bottomHeadSink = Sink.last();	 

		final Flow<Integer, Integer, NotUsed> doubler = Flow.of(Integer.class)
				 													.map(elem -> elem * 2);

		final Flow<Integer, Integer, NotUsed> tripler = Flow.of(Integer.class).map(elem -> elem * 3);

		 final RunnableGraph<Pair<CompletionStage<Done>, CompletionStage<Integer>>> result =
				 RunnableGraph.<Pair<CompletionStage<Done>, CompletionStage<Integer>>>fromGraph(
	// GraphDSL create method from create() to create21() 
						 GraphDSL.create(
				 			topHeadSink,
				 			bottomHeadSink,
				 			Keep.both(),
				 			(builder , top, bottom) -> {
				 		        final UniformFanOutShape<Integer, Integer> bcast = builder.add(Broadcast.create(2));
				 		        builder.from(builder.add(Source.from(Arrays.asList(1,10))))
				 		        		.viaFanOut(bcast)
				 		        		.via(builder.add(doubler)).to(top);
				 				
				 		        builder.from(bcast)
				 		        		.via(builder.add(tripler))
				 		        		.to(bottom);
				 		        
				 				return ClosedShape.getInstance();
				 				
				 			}
				 			
				 	));
//		 result.run(materializer);

// 2.  Getting result using  pait.second() or pait.first()
		 final Pair<CompletionStage<Done>, CompletionStage<Integer>> pair = result.run(materializer);
			Integer list;
			try {
				list = pair.second().toCompletableFuture().get(3, TimeUnit.SECONDS);
				log.debug("bottomHeadSink "+list);

			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			} catch (TimeoutException e1) {
				e1.printStackTrace();
			}
	}

	public static void main(String[] args) {
		BroadcastFlowsUsingGraphApp app = new BroadcastFlowsUsingGraphApp();
		app.graphDSLModel();
	}
}
