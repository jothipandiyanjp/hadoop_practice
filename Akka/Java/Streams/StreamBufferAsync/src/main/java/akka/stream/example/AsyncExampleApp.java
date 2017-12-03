package akka.stream.example;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class AsyncExampleApp
{
	private final ActorSystem system = ActorSystem.create("stream-composition");
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	public void asyncModel(){
		Source.range(1, 5)
			   .map( i -> {log.debug("A : "+i); return i;}).async()
			   .map( i -> {log.debug("B : "+i); return i;}).async()
			   .map( i -> {log.debug("C : "+i); return i;}).async()
			   .runWith(Sink.ignore(), materializer);

	}
	
    public static void main( String[] args )
    {
    		AsyncExampleApp app = new AsyncExampleApp();
    		app.asyncModel();
    }
}
