package akka.stream.example;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.ClosedShape;
import akka.stream.FlowShape;
import akka.stream.Inlet;
import akka.stream.Materializer;
import akka.stream.Outlet;
import akka.stream.SinkShape;
import akka.stream.SourceShape;
import akka.stream.UniformFanInShape;
import akka.stream.UniformFanOutShape;
import akka.stream.javadsl.Balance;
import akka.stream.javadsl.Broadcast;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.Merge;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class ComplexSystemModel2App {

	private final ActorSystem system = ActorSystem.create("stream-composition");
	private final LoggingAdapter log  = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	
	public void complexSystem(){
		RunnableGraph<NotUsed> graph = RunnableGraph.fromGraph(
			      GraphDSL.create(builder -> {
			      final SourceShape<Integer> A = builder.add(Source.single(0));
			      final UniformFanOutShape<Integer, Integer> B = builder.add(Broadcast.create(2));
			      final UniformFanInShape<Integer, Integer> C = builder.add(Merge.create(2));
			      final FlowShape<Integer, Integer> D =
			        builder.add(Flow.of(Integer.class).map(i -> i + 1));
			      final UniformFanOutShape<Integer, Integer> E = builder.add(Balance.create(2));
			      final UniformFanInShape<Integer, Integer> F = builder.add(Merge.create(2));
			      final SinkShape<Integer> G = builder.add(Sink.foreach(System.out::println));
			      builder.from(F.out()).toInlet(C.in(0));
			      builder.from(A).toInlet(B.in());
			      builder.from(B.out(0)).toInlet(C.in(1));
			      builder.from(C.out()).toInlet(F.in(0));
			      builder.from(B.out(1)).via(D).toInlet(E.in());
			      builder.from(E.out(0)).toInlet(F.in(1));
			      builder.from(E.out(1)).to(G);
			      return ClosedShape.getInstance();
			    }));
		graph.run(materializer);

	}

	
	public static void main(String[] args) {
		ComplexSystemModel2App app =new ComplexSystemModel2App();
		app.complexSystem();
	}
	
}
