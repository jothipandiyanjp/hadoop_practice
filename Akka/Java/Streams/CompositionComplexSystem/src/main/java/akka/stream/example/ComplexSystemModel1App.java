package akka.stream.example;

import java.util.concurrent.CompletionStage;

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

public class ComplexSystemModel1App {

	private final ActorSystem system = ActorSystem.create("stream-composition");
	private final LoggingAdapter log  = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	
	public void complexSystem(){

		RunnableGraph<NotUsed> graph =  RunnableGraph.<NotUsed>fromGraph(
	    	      GraphDSL.create(builder -> {
	    	          final Outlet<Integer> A = builder.add(Source.single(0)).out();
	    	          final UniformFanOutShape<Integer, Integer> B = builder.add(Broadcast.create(2));
	    	          final UniformFanInShape<Integer, Integer> C = builder.add(Merge.create(2));
	    	    	  
	    	          final FlowShape<Integer, Integer> D = builder.add(Flow.of(Integer.class).map(i ->i + 1));
	    	          final UniformFanOutShape<Integer, Integer> E = builder.add(Balance.create(2));
	    	          final UniformFanInShape<Integer, Integer> F = builder.add(Merge.create(2));
	    	          
	    	          final Inlet<Integer> G = builder.add(Sink.<Integer> foreach(System.out::println)).in();
	    	          
	    	          builder.from(F).toFanIn(C);
	    	          builder.from(A).viaFanOut(B).viaFanIn(C).toFanIn(F);
	    	          builder.from(B).via(D).viaFanOut(E).toFanIn(F);
	    	          builder.from(E).toInlet(G);

					return ClosedShape.getInstance();
	    	    	  
	    	      })

	    		);
	    		
		graph.run(materializer);
		
	}

	
	public static void main(String[] args) {
		ComplexSystemModel1App app =new ComplexSystemModel1App();
		app.complexSystem();
	}
	
}
