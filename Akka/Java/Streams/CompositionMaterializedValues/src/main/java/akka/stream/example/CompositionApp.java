package akka.stream.example;

import java.util.Optional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function2;
import akka.japi.Pair;
import akka.japi.function.Procedure;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Tcp;
import akka.stream.javadsl.Tcp.OutgoingConnection;
import akka.util.ByteString;

public class CompositionApp {

	private final ActorSystem system = ActorSystem.create("stream-composition");
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	public void nestedFlow() {
		final Source<Integer, CompletableFuture<Optional<Integer>>> source = Source.<Integer>maybe();
		
		final Flow<Integer, Integer, NotUsed> flow1 = Flow.of(Integer.class).take(100);		
		
		final Source<Integer, CompletableFuture<Optional<Integer>>> nestedSource =
				  source.viaMat(flow1, Keep.left()).named("nestedSource");		
		
		final Flow<Integer, ByteString, NotUsed> flow2 = Flow.of(Integer.class)
				  .map(i -> ByteString.fromString(i.toString()));

		final Flow<ByteString, ByteString, CompletionStage<OutgoingConnection>> flow3 =
				  Tcp.get(system).outgoingConnection("localhost", 8080);
		
				

		final Flow<Integer, ByteString, CompletionStage<OutgoingConnection>> nestedFlow =
				  flow2.viaMat(flow3, Keep.right()).named("nestedFlow");
				
		final Sink<ByteString, CompletionStage<String>> sink =
			    Sink.<String, ByteString> fold("", (acc, i) -> acc + i.utf8String());
		
		final Sink<Integer, Pair<CompletionStage<OutgoingConnection>, CompletionStage<String>>> nestedSink =
				  nestedFlow.toMat(sink, Keep.both());
		
		final RunnableGraph<CompletionStage<MyClass>> runnableGraph = nestedSource.toMat(nestedSink,Combiner::f);

		runnableGraph.run(materializer).whenComplete((i , j) ->System.out.println(i.p) );
		
	}

	public static void main(String[] args) {
		CompositionApp app = new CompositionApp();
		app.nestedFlow();
	}

	
	static class MyClass {
		  private CompletableFuture<Optional<Integer>> p;
		  private OutgoingConnection conn;
		 
		  public MyClass(CompletableFuture<Optional<Integer>> p, OutgoingConnection conn) {
		    this.p = p;
		    this.conn = conn;
		  }
		 
		  public void close() {
		    p.complete(Optional.empty());
		  }
		}
		 
		static class Combiner {
		  static CompletionStage<MyClass> f(CompletableFuture<Optional<Integer>> p,
		      Pair<CompletionStage<OutgoingConnection>, CompletionStage<String>> rest) {
		    return rest.first().thenApply(c -> new MyClass(p, c));
		  }
		}
}

