package akka.stream.bascis;

import java.util.stream.Stream;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.FlowShape;
import akka.stream.Fusing;
import akka.stream.Graph;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class UsingFusionApp {
	private final ActorSystem system = ActorSystem.create("akka-stream");
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	public void example(){
		Flow<Integer, Integer, NotUsed> flow = Flow.of(Integer.class)
													.map( x  -> x * 2 )
													.filter( x -> x > 500 );
		
		Graph<FlowShape<Integer, Integer>, NotUsed>  fused = Fusing.aggressive(flow);
		Source.fromIterator( () -> Stream.iterate(0, x -> x + 1).iterator())
			   .via(fused)
			   .take(1000).runForeach(x -> {log.debug(""+x);}, materializer);
	
		
}
	public static void main(String[] args) {

		UsingFusionApp app = new UsingFusionApp();
		app.example();


	}
}
