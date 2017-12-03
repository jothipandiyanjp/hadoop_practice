package akka.stream.example;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import scala.Function1;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.function.Procedure;
import akka.stream.ActorMaterializer;
import akka.stream.Attributes;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Sink;

public class SeqApp {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	
	public void drainSourceToList(){
		
        final Source<Integer, NotUsed> mySource = Source.range(1, 1000);
        //#draining-to-list-unsafe
        
        // Dangerous: might produce a collection with 2 billion elements!       
        final CompletionStage<List<Integer>> strings = mySource.map(i->i).runWith(Sink.seq(), materializer);
        try {
        	log.debug(""+strings.toCompletableFuture().get(Integer.MAX_VALUE, TimeUnit.SECONDS));
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}

	}
	
	public void drainSourceToListWithLimit(){
        final Source<Integer, NotUsed> mySource = Source.range(1, Integer.MAX_VALUE);
//        final Source<Integer, NotUsed> mySource = Source.range(1, 10);

        final int MAX_ALLOWED_SIZE = 100;

        // OK. Future will fail with a `StreamLimitReachedException`
        final CompletionStage<List<Integer>> strings = mySource.limit(MAX_ALLOWED_SIZE)
        														.runWith(Sink.seq(), materializer);
        try {
        	log.debug(""+strings.toCompletableFuture().get(3, TimeUnit.SECONDS));
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
	}
	
	public void drainSourceToListWithTake(){
        final Source<String, NotUsed> mySource = Source.from(Arrays.asList("1", "2", "3"));

        final int MAX_ALLOWED_SIZE = 100;
        
        // OK. Collect up until max-th elements only, then cancel upstream
        final CompletionStage<List<String>> strings = mySource.take(MAX_ALLOWED_SIZE).runWith(Sink.seq(), materializer);
        try {
        	System.out.println(strings.toCompletableFuture().get(3, TimeUnit.SECONDS));
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}

	}
	public static void main(String[] args) {
		SeqApp example = new SeqApp();
//		example.drainSourceToList();
//		example.drainSourceToListWithLimit();
		example.drainSourceToListWithTake();
	}
}
