package akka.stream.flowshape;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Graph;
import akka.stream.Materializer;
import akka.stream.SourceShape;
import akka.stream.impl.fusing.Map;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.stage.GraphStage;

public class FlowShapeApp {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log  = Logging.getLogger(system, this);
	private final  Materializer mat = ActorMaterializer.create(system);

	
	public void counterModel(){
		
		Source<Integer, NotUsed> source = Source.from(Arrays.asList(1,2,3,4,5,6,7));

		source .via(new Filter<Integer>((n) -> n % 2 == 0))  // 2 2 4 4 6 6
        .via(new Duplicator<Integer>())
        .via(new CustomMap<Integer, Integer>((n) -> n / 2))  //   11 2 2  3 3
        .runWith(Sink.foreach(i -> log.debug(""+i)), mat);
	}
	
	public static void main(String[] args) {
		FlowShapeApp app = new FlowShapeApp();
		app.counterModel();
	}
}
