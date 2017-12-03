package akka.stream.example;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.*;
import akka.japi.function.Creator;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.SourceQueueWithComplete;

public class FlowApp {

	private final ActorSystem system = ActorSystem.create("akka-stream");
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	public void groupBy() {
	    final Iterable<String> input = Arrays.asList("Aaa", "Abb", "Bcc", "Cdd", "Cee","Def","Deg");
	    final Flow<String, List<String>, NotUsed> flow = Flow
	    		  .of(String.class)
	    	        .groupBy(4, elem -> elem.substring(0, 1))
	    	        .grouped(3)
	    	        .mergeSubstreams();

	    final CompletionStage<List<List<String>>> future =
	            Source.from(input).via(flow).limit(10).runWith(Sink.<List<String>> seq(), materializer);
	        try {
	        	List<List<String>> list = future.toCompletableFuture().get(1, TimeUnit.SECONDS);
	        	System.out.println(list);
	        
	        } catch (InterruptedException | ExecutionException
					| TimeoutException e) {
				e.printStackTrace();
			}
	      
		
	}

	public void sample() {
		groupBy();
	}

	public static void main(String[] args) {
		FlowApp app = new FlowApp();
		app.sample();
	}
}
