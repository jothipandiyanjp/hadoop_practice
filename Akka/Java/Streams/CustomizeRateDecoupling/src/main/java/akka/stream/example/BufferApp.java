package akka.stream.example;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.function.Procedure;
import akka.stream.ActorMaterializer;
import akka.stream.Graph;
import akka.stream.Materializer;
import akka.stream.SourceShape;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.stage.GraphStage;

public class BufferApp {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log  = Logging.getLogger(system, this);
	private final  Materializer mat = ActorMaterializer.create(system);

	
	public void counterModel(){
		Source<Integer, NotUsed> source = Source.from(Arrays.asList(1,2,3,4,5));

		source.via(new TwoBuffer())
			.runWith(Sink.foreach(new Procedure<Object>() {
				public void apply(Object m1) throws Exception {
					log.debug(""+m1);
				};
			}), mat);
	}	
	public static void main(String[] args) {
			BufferApp app = new BufferApp();
		app.counterModel();
	}
}
