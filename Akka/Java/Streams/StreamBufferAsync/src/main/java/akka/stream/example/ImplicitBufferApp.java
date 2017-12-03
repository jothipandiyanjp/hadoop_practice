package akka.stream.example;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.FiniteDuration;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Attributes;
import akka.stream.ClosedShape;
import akka.stream.FanInShape2;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.ZipWith;

public class ImplicitBufferApp
{
	private final ActorSystem system = ActorSystem.create("stream-composition");
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	public void asyncModel(){
		final FiniteDuration oneSecond = FiniteDuration.create(1, TimeUnit.SECONDS);

		// Fast Producer. So, that 
		final Source<String, Cancellable> msgSource = Source.tick(oneSecond.mul(1), oneSecond.mul(1), "message..!"); 

		// slow Producer
		final Source<String, Cancellable> tickSource = Source.tick(oneSecond.mul(3), oneSecond.mul(3), "tick");
		
		final Flow<String, Integer, NotUsed> conflate = Flow.of(String.class)
															.conflateWithSeed( first -> 1, (count,elem) -> {
																log.debug("count"+count);
																return count+1;});
		
		RunnableGraph.fromGraph(GraphDSL.create(b -> {

			/*
			 * Internal buffer size is 16. We are setting buffersize to 1.
			 * Try without withAttribute. 
			 */			
			
			final FanInShape2<String, Integer, Integer> zipper =
					  b.add(ZipWith.create((String tick, Integer count) -> count).async() .withAttributes(Attributes.inputBuffer(1, 1)));
			  
			  b.from(b.add(msgSource)).via(b.add(conflate)).toInlet(zipper.in1());
			  
			  b.from(b.add(tickSource)).toInlet(zipper.in0());
			  
			  b.from(zipper.out()).to(b.add(Sink.foreach(elem -> log.debug(""+elem))));

			return ClosedShape.getInstance();
			
		})).run(materializer);

	}
	
    public static void main( String[] args )
    {
    		ImplicitBufferApp app = new ImplicitBufferApp();
    		app.asyncModel();
    }
}
