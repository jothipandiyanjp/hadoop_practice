package multi.source.sink.combining.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Broadcast;
import akka.stream.javadsl.Merge;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;



public class CombiningSinkApp {

	private final ActorSystem system = ActorSystem.create("graph-dsl");
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	public void combine(){
		
		final ActorRef ref = system.actorOf(Props.create(MyActor.class),"myActor");
		
		 Sink<Integer, NotUsed> sendRemotely = Sink.actorRef(ref,"Done");
		 Sink<Integer, CompletionStage<Done>> localProcessing = Sink.<Integer>foreach(a -> log.debug("local message -> "+a));
		
		 Sink<Integer, NotUsed> sinks = Sink.combine(sendRemotely,localProcessing, new ArrayList<>(), a -> Broadcast.create(a));

		
		Source.<Integer>from(Arrays.asList(new Integer[]{0,1,2,3}))
					.runWith(sinks, materializer);
		
	}
	
	public static void main(String[] args) {
		CombiningSinkApp app = new CombiningSinkApp();
		app.combine();
	}
}
