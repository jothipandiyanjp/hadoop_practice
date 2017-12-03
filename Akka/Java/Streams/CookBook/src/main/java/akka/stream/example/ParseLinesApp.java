package akka.stream.example;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import akka.stream.FlowShape;
import akka.stream.Inlet;
import akka.stream.Materializer;
import akka.stream.Outlet;
import akka.stream.javadsl.Framing;
import akka.stream.javadsl.FramingTruncation;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Sink;
import akka.stream.stage.AbstractInHandler;
import akka.stream.stage.AbstractOutHandler;
import akka.stream.stage.GraphStage;
import akka.stream.stage.GraphStageLogic;
import akka.util.ByteString;

public class ParseLinesApp {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	
	public void digestExample(){
	    final Source<ByteString, NotUsed> rawData = Source.from(Arrays.asList(
	    	      ByteString.fromString("Hello World"),
	    	      ByteString.fromString("\r"),
	    	      ByteString.fromString("!\r"),
	    	      ByteString.fromString("\nHello Akka!\r\nHello Streams!"),
	    	      ByteString.fromString("\r\n\r\n")));
	    
	    final Source<String, NotUsed> lines = rawData
	    	      .via(Framing.delimiter(ByteString.fromString("\r\n"), 100, FramingTruncation.ALLOW))
	    	      .map(b -> b.utf8String());
	    List<String> list = null;
		try {
			list = lines.limit(10).runWith(Sink.seq(), materializer).toCompletableFuture().get(1, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
	    log.debug(""+list);
	}
	public static void main(String[] args) {
		ParseLinesApp example = new ParseLinesApp();
		example.digestExample();
	}
}
