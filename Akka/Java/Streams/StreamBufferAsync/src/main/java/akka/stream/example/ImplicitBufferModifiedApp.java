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
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.ZipWith;

/*
 * Run this program for 2-3 minutes and debug output
 * 
*/
public class ImplicitBufferModifiedApp
{
	private final ActorSystem system = ActorSystem.create("stream-composition");
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	
	 
	public void asyncModel(){
		final FiniteDuration oneSecond = FiniteDuration.create(1, TimeUnit.SECONDS);

		// Fast Producer. So, that this was handle by conflate or conflateSwithSeed()

		final Source<String, Cancellable> msgSource1 = Source.tick(oneSecond.mul(1), oneSecond.mul(1), "message..!"); 
		final Source<String, Cancellable> msgSource = msgSource1;
		msgSource.buffer(5, OverflowStrategy.backpressure());
		
		// slow Producer
		final Source<String, Cancellable> tickSource = Source.tick(oneSecond.mul(3), oneSecond.mul(3), "tick");
		
		// conflate(s1,s2) => write aggregator function
		final Flow<String, String, NotUsed> conflate = Flow.of(String.class)
															.conflate((count,elem) -> {  
																//log.debug("count"+count);
																System.out.println("count -> " + count);
																return count+elem;});
		
		RunnableGraph.fromGraph(GraphDSL.create(b -> {
			  final FanInShape2<String, String, String> zipper =
					  b.add(ZipWith.create((String tick, String mess) -> {
						  System.out.println(tick+" -> "+mess);
						  return tick+" -> "+mess;}).async());
			
			/*
			 *  Try  without conflate flow	
			 *  
			*/
			  b.from(b.add(msgSource)).via(b.add(conflate)).toInlet(zipper.in1());
			// b.from(b.add(msgSource)).toInlet(zipper.in1());

	
			  
			  
			  b.from(b.add(tickSource)).toInlet(zipper.in0());
			  
			  b.from(zipper.out()).to(b.add(Sink.foreach(elem -> log.debug(""+elem))));

			return ClosedShape.getInstance();
			
		})).run(materializer);

	}
	
    public static void main( String[] args )
    {
    		ImplicitBufferModifiedApp app = new ImplicitBufferModifiedApp();
    		app.asyncModel();
    }
}
