package akka.stream.example;

import java.util.Arrays;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.FlowShape;
import akka.stream.Materializer;
import akka.stream.UniformFanInShape;
import akka.stream.UniformFanOutShape;
import akka.stream.javadsl.Balance;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Merge;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class ParallelApp {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer mat = ActorMaterializer.create(system);

	static class ScoopOfBatter {
	}



	static class Pancake {
	}

	public void demonstrate() {
		 Flow<ScoopOfBatter, Pancake, NotUsed> fryingPan =
			      Flow.of(ScoopOfBatter.class).map(batter -> new Pancake());


		    Flow<ScoopOfBatter, Pancake, NotUsed> pancakeChef =
		    	      Flow.fromGraph(GraphDSL.create(b -> {
		    	          final UniformFanInShape<Pancake, Pancake> mergePancakes =
		    	                  b.add(Merge.create(2));
		    	          final UniformFanOutShape<ScoopOfBatter, ScoopOfBatter> dispatchBatter =
		    	                  b.add(Balance.create(2));
		    	          b.from(dispatchBatter.out(0)).via(b.add(fryingPan)).toInlet(mergePancakes.in(0));
		    	          b.from(dispatchBatter.out(1)).via(b.add(fryingPan)).toInlet(mergePancakes.in(1));
		    	          return FlowShape.of(dispatchBatter.in(), mergePancakes.out());
		    	      }));

		   Source<ScoopOfBatter, NotUsed>  totalChildrens = Source.from(Arrays.asList(new ScoopOfBatter()));
		   
		   totalChildrens.runWith(pancakeChef.toMat(Sink.foreach(i->log.debug("Pancake ready")),Keep.none()), mat);

	}

	public static void main(String[] args) {
		ParallelApp app = new ParallelApp();
		app.demonstrate();
	}

}
