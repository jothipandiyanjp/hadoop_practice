package akka.stream.bascis;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;

import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.function.Procedure;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class SourceSinkApp {
	private final ActorSystem system = ActorSystem.create("akka-stream");
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	public void example(){
      //  1. Source 
		final Source<Integer, NotUsed> source = Source.from(Arrays.asList(1,2,3,4,5,6,7,8,9,10));
		
	  //  2. Sink	
		final Sink<Integer, CompletionStage<Integer>> sink =  Sink.<Integer, Integer> fold(0, (aggr, next) -> aggr + next);
	  
	  //  3. Runnable Graph 
		final RunnableGraph<CompletionStage<Integer>> runnable = source.toMat(sink, Keep.right());
	
	  //  4. CompletionStage
		final CompletionStage<Integer> sum = runnable.run(materializer);
		
//--------------------------
		
		// FLow with  source and sink
		Flow.of(Double.class).map((x) -> x.intValue()).runWith(Source.from(Arrays.asList(1.3,4.4)), 
											Sink.foreach(a -> {log.debug(""+a);}), materializer);

		// Source.tick() example
		FiniteDuration f =Duration.create(1, "seconds");
		Source.tick(f,	f, 1).runWith(Sink.foreach(x ->  log.debug(""+x)), materializer);
	}
	public static void main(String[] args) {

		SourceSinkApp app = new SourceSinkApp();
		app.example();


	}
}
