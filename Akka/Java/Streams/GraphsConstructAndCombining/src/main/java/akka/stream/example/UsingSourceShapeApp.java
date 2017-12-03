package akka.stream.example;


import java.util.Iterator;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.ClosedShape;
import akka.stream.FanInShape;
import akka.stream.FanInShape2;
import akka.stream.FanInShape3;
import akka.stream.SourceShape;
import akka.stream.UniformFanInShape;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Zip;
import akka.stream.javadsl.ZipN;
import scala.inline;

public class UsingSourceShapeApp {
	private final ActorSystem system = ActorSystem.create("akka");
	private final LoggingAdapter log  = Logging.getLogger(system , this);
	private final ActorMaterializer materializer = ActorMaterializer.create(system);
	
	public void combiningGraph(){
		final Source<Integer, NotUsed> ints = Source.fromIterator(() -> new Ints());

		final Source<Pair<Integer, Integer>, NotUsed> pairs = Source.fromGraph(GraphDSL.create(builder -> {
			final FanInShape2<Integer, Integer, Pair<Integer, Integer>> zip = builder.add(Zip.create());

			builder.from(builder.add(ints.filter(i -> i % 2 == 0))).toInlet(zip.in0());
			builder.from(builder.add(ints.filter(i -> i % 2 == 1))).toInlet(zip.in1());

			return SourceShape.of(zip.out());
		}));

		final CompletionStage<Pair<Integer, Integer>> firstPair = pairs.runWith(Sink.<Pair<Integer, Integer>> head(),
				materializer);
		//
		firstPair.whenComplete((result, failure) -> {
			printWithExampleInfo("Fourth example").accept(result);
		});
	}
	private  Consumer<Pair<Integer,Integer>> printWithExampleInfo(String exampleInfo) {
		return p -> log.debug(exampleInfo + " " + p);
	}
	public static void main(String[] args) {

		UsingSourceShapeApp app = new UsingSourceShapeApp();
		app.combiningGraph();
	}
}


class Ints implements Iterator<Integer>{
	private int next = 0;
	public boolean hasNext() {
		return true;
	}
	public Integer next() {
		return next++;
	}
	
}