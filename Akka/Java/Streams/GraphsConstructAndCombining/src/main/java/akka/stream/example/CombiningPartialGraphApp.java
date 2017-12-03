package akka.stream.example;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.function.Creator;
import akka.stream.ActorMaterializer;
import akka.stream.ClosedShape;
import akka.stream.FanInShape;
import akka.stream.FanInShape2;
import akka.stream.Graph;
import akka.stream.Inlet;
import akka.stream.Materializer;
import akka.stream.UniformFanInShape;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.ZipWith;
import akka.stream.javadsl.Source;


public class CombiningPartialGraphApp {

	private final ActorSystem system = ActorSystem.create("graph-dsl");
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	public void combine(){		
		
		// creating fan-in (2 inputs)
		final Graph<FanInShape2<Integer, Integer, Integer>, NotUsed> zip = ZipWith
																			.create( (Integer left , Integer right)
																-> Math.max(left, right));

		final Graph<UniformFanInShape<Integer, Integer>, NotUsed> pickMaxOfThree = 
				GraphDSL.create(builder -> {
					final FanInShape2<Integer, Integer, Integer> zip1 = builder.add(zip);
					final FanInShape2<Integer, Integer, Integer> zip2 = builder.add(zip);
					builder.from(zip1.out()).toInlet(zip2.in0());
					
					return new UniformFanInShape<Integer, Integer>(zip2.out(), new Inlet[]{zip1.in0(), zip1.in1(), zip2.in1()});
				});

		
		// Sink
		final Sink<Integer, CompletionStage<Integer>> resultSink = Sink.<Integer>head();
		RunnableGraph<CompletionStage<Integer>> result = 
		RunnableGraph.fromGraph(
				GraphDSL.create(
							resultSink,
							(builder, sink) -> {
								final UniformFanInShape<Integer, Integer> pm = builder.add(pickMaxOfThree);

								builder.from(builder.add(Source.single(1))).toInlet(pm.in(0));
								builder.from(builder.add(Source.single(4))).toInlet(pm.in(1));
								builder.from(builder.add(Source.single(3))).toInlet(pm.in(2));
								builder.from(pm.out()).to(sink);
								return ClosedShape.getInstance();
							}
						));
		
		final CompletionStage<Integer> max = result.run(materializer);
		
		max.whenComplete((x, y ) -> log.debug("Max number is "+x));  //returns 4
	}
	
	public static void main(String[] args) {
		CombiningPartialGraphApp app = new CombiningPartialGraphApp();
		app.combine();
	}
}
