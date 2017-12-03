package akka.stream.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.function.Creator;
import akka.japi.function.Procedure;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.StreamConverters;
import akka.util.ByteString;

public class IoStreamApp {

	private final ActorSystem system = ActorSystem.create("akka-stream");
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	final List<Integer> list = Arrays.asList(1, 2, 3);

	IntFunction<Integer> intIdentity = new IntFunction<Integer>() {
		@Override
		public Integer apply(int value) {
			return value;
		}
	};

	public void javaCollectorParallelUnordered(){	
    
		Collector<Integer, ?, Integer> intFunction =Collectors.summingInt(new ToIntFunction<Integer>() {
			@Override
			public int applyAsInt(Integer value) {
				return value;
			}
		});
        			
		 Sink<Integer, CompletionStage<Integer>> sink = StreamConverters.javaCollectorParallelUnordered(10, () -> intFunction);

	      try {
	    	  Integer i =Source.range(4 , 6).runWith(sink,materializer).toCompletableFuture().get();
	    	  log.debug(""+i);
	      } catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

    }

	public void javaCollector() {
		final Sink<Integer, CompletionStage<List<Integer>>> collectorSink = StreamConverters
				.javaCollector(Collectors::toList);
		CompletionStage<List<Integer>> result = Source.from(list).runWith(
				collectorSink, materializer);
		result.whenComplete((i, j) -> log.debug("" + i));

	}

	public void fromJavaStreams() {
		CompletionStage<List<Integer>> result = StreamConverters
				.fromJavaStream(() -> list.stream()).map(i -> i.intValue())
				.runWith(Sink.seq(), materializer);

		result.whenComplete((i, j) -> log.debug("" + i));

	}

	public void asJavaStream() {
		final Sink<Integer, Stream<Integer>> streamSink = StreamConverters
				.asJavaStream();
		java.util.stream.Stream<Integer> javaStream = Source.from(list)
				.runWith(streamSink, materializer);
		javaStream.forEach(i -> log.debug("" + i));
	}

	public void asOutputStream() {
		final FiniteDuration timeout = FiniteDuration.create(300,
				TimeUnit.MILLISECONDS);
		final Source<ByteString, OutputStream> source = StreamConverters
				.asOutputStream(timeout);
		final OutputStream s = source.to(
				Sink.foreach(new Procedure<ByteString>() {
					private static final long serialVersionUID = 1L;

					public void apply(ByteString elem) {
						log.debug(elem.decodeString("utf8"));
					}
				})).run(materializer);

		try {
			s.write("ab".getBytes());
		} catch (IOException e) {
			log.error("Exception Occured : " + e.getMessage());
		}

	}

	public void asInputStream() {
		final FiniteDuration timeout = FiniteDuration.create(10000,
				TimeUnit.MILLISECONDS);
		final Sink<ByteString, InputStream> sink = StreamConverters
				.asInputStream(timeout);
		final List<ByteString> list = Collections.singletonList(ByteString
				.fromString("abcdefghijklmnopqrstuvwxyz"));
		final InputStream stream = Source.from(list)
				.runWith(sink, materializer);

		byte[] a = new byte[26];
		try {
			stream.read(a);
			stream.close();
		} catch (IOException e) {
			log.error("Exception Occured : " + e.getMessage());
		}

		for (byte b : a)
			log.debug("" + (char) b);
	}

	public void fromInputStream() {
		Creator<InputStream> creator = new Creator<InputStream>() {
			@Override
			public InputStream create() throws Exception {
				URL url = new URL(
						"http://finance.yahoo.com/webservice/v1/symbols/INFY/quote?format=json&view=detail");
				return url.openStream();
			}
		};

		Source<ByteString, CompletionStage<IOResult>> source = StreamConverters
				.fromInputStream(creator);

		while (true) {
			source.runWith(
					Sink.foreach(i -> log.debug("\n" + i.decodeString("utf8"))),
					materializer);
		}
	}

	public void fromOutputStream() {
		final OutputStream os = new OutputStream() {
			volatile int left = 3;

			@Override
			public void write(int data) throws IOException {
				if (left == 0) {
					throw new RuntimeException("Can't accept more data.");
				}
				left--;
			}
		};

		final CompletionStage<IOResult> resultFuture = Source.single(
				ByteString.fromString("12345")).runWith(
				StreamConverters.fromOutputStream(() -> os), materializer);
		// final CompletionStage<IOResult> resultFuture =
		// Source.single(ByteString.fromString("123")).runWith(StreamConverters.fromOutputStream(()
		// -> os), materializer);
		try {
			final IOResult result = resultFuture.toCompletableFuture().get(300,
					TimeUnit.MILLISECONDS);
			log.debug(" " + result.status());
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			log.error("Exception Occured : " + e.getMessage());
		}

	}

	public static void main(String[] args) {
		IoStreamApp app = new IoStreamApp();
		// app.fromOutputStream();
		// app.fromInputStream();
		// app.asInputStream();
		// app.asOutputStream();
		// app.asJavaStream();
		// app.fromJavaStreams();
		// app.javaCollector();
		  app.javaCollectorParallelUnordered();
	}
}
