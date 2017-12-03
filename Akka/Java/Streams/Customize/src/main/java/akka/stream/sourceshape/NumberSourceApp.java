package akka.stream.sourceshape;

import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Graph;
import akka.stream.Materializer;
import akka.stream.SourceShape;
import akka.stream.javadsl.Source;
import akka.stream.stage.GraphStage;

public class NumberSourceApp {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log  = Logging.getLogger(system, this);
	private final  Materializer mat = ActorMaterializer.create(system);

	
	public void counterModel(){
		Graph<SourceShape<Integer>, NotUsed> sourceGraph= new NumbersSource();
		
		Source<Integer, NotUsed> source = Source.fromGraph(sourceGraph);

		CompletionStage<Integer> result = source.take(10).filter(  i -> (i%2==0)).runFold(0,
												(acc, ele) ->acc+ele,  mat);

		result.whenComplete((x,y) -> log.debug(""+x));
	}
	
	public static void main(String[] args) {
		NumberSourceApp app = new NumberSourceApp();
		app.counterModel();
	}
}
