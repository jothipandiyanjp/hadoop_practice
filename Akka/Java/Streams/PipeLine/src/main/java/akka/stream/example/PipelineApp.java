package akka.stream.example;

import java.util.Arrays;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class PipelineApp {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer mat = ActorMaterializer.create(system);

	static class ScoopOfBatter {
	}

	static class HalfCookedPancake {
	}

	static class Pancake {
	}

	public void demonstrate() {
		   Flow<ScoopOfBatter, HalfCookedPancake, NotUsed> fryingPan1 =
				      Flow.of(ScoopOfBatter.class).<HalfCookedPancake>map(batter -> 
				    		  new HalfCookedPancake() 
				    );

		   Flow<HalfCookedPancake, Pancake, NotUsed> fryingPan2 =
				      Flow.of(HalfCookedPancake.class).map(batter -> new Pancake());

		   Flow<ScoopOfBatter, Pancake, NotUsed> pancakeChef =  fryingPan1.via(fryingPan2);
		   
		   Source<ScoopOfBatter, NotUsed>  totalChildrens = Source.from(Arrays.asList(new ScoopOfBatter()));
		   
		   totalChildrens.runWith(pancakeChef.toMat(Sink.foreach(i->log.debug("Pancake ready")),Keep.none()), mat);

	}

	public static void main(String[] args) {
		PipelineApp app = new PipelineApp();
		app.demonstrate();
	}

}
