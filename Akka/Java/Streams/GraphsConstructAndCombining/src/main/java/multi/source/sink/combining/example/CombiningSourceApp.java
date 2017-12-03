package multi.source.sink.combining.example;

import java.util.ArrayList;
import java.util.Arrays;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Merge;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;



public class CombiningSourceApp {

	private final ActorSystem system = ActorSystem.create("graph-dsl");
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	public void combine(){
		Source<Integer, NotUsed> source1 = Source.single(1);
		Source<Integer, NotUsed> source2 = Source.single(5);

		final Source<Integer, NotUsed> source = Source.combine(source1, source2, Arrays.asList(source1,source1,source2), i -> Merge.create(i));
		
		source.runWith(Sink.fold(0,(a,b) -> a + b ), materializer).whenComplete(
				(x,y)-> log.debug(""+x)
				);
		
	}
	
	public static void main(String[] args) {
		CombiningSourceApp app = new CombiningSourceApp();
		app.combine();
	}
}
