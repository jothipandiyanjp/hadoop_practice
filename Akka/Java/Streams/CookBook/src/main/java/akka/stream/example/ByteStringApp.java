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
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Sink;
import akka.stream.stage.AbstractInHandler;
import akka.stream.stage.AbstractOutHandler;
import akka.stream.stage.GraphStage;
import akka.stream.stage.GraphStageLogic;
import akka.util.ByteString;

public class ByteStringApp {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	class DigestCalculator extends GraphStage<FlowShape<ByteString, ByteString>> {
	    private final String algorithm;
	    public Inlet<ByteString> in = Inlet.<ByteString>create("DigestCalculator.in");
	    public Outlet<ByteString> out = Outlet.<ByteString>create("DigestCalculator.out");
	    private FlowShape<ByteString, ByteString> shape = FlowShape.of(in, out);

	    public DigestCalculator(String algorithm) {
	      this.algorithm = algorithm;
	    }

	    @Override
	    public FlowShape<ByteString, ByteString> shape() {
	      return shape;
	    }

	    @Override
	    public GraphStageLogic createLogic(Attributes inheritedAttributes) {
	      return new GraphStageLogic(shape) {
	        final MessageDigest digest;

	        {
	          try {
	            digest = MessageDigest.getInstance(algorithm);
	          } catch(NoSuchAlgorithmException ex) {
	            throw new RuntimeException(ex);
	          }

	          setHandler(out, new AbstractOutHandler() {
	            @Override
	            public void onPull() throws Exception {
	              pull(in);
	            }
	          });
	          setHandler(in, new AbstractInHandler() {
	            @Override
	            public void onPush() throws Exception {
	              ByteString chunk = grab(in);
	              System.out.println(chunk.decodeString("utf8"));
	              digest.update(chunk.toArray());
	              pull(in);
	            }

	            @Override
	            public void onUpstreamFinish() throws Exception {
	              // If the stream is finished, we need to emit the digest
	              // before completing
	              emit(out, ByteString.fromArray(digest.digest()));
	              completeStage();
	            }
	          });
	        }


	      };
	    }

	  }
	
	public void digestExample(){
		 Source<ByteString, NotUsed> data = Source.from(Arrays.asList(
		          ByteString.fromString("abac"),
		          ByteString.fromString("abac")));

		  final Source<ByteString, NotUsed> digest = data
		          .via(new DigestCalculator("SHA-256"));
		  try {
			ByteString got = digest.runWith(Sink.head(), materializer).toCompletableFuture().get(3, TimeUnit.SECONDS);
			log.debug("");
		
		  } catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}		  
	}
	public static void main(String[] args) {
		ByteStringApp example = new ByteStringApp();
		example.digestExample();
	}
}
