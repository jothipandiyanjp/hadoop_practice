package akka.stream.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletionStage;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Sink;
import akka.util.ByteString;

public class FileIOApp {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer mat = ActorMaterializer.create(system);

	public void fromFile() {
		final Path file = Paths.get("src/main/resources/application.conf");

		Sink<ByteString, CompletionStage<Done>> printlnSink = Sink
				.<ByteString> foreach(chunk -> log.debug("\n"+chunk
						.utf8String()));

		 CompletionStage<IOResult> ioResult =
				    FileIO.fromPath(file)
				      .to(printlnSink)
				      .run(mat);
	}

	public static void main(String[] args) {
		FileIOApp app = new FileIOApp();
		app.fromFile();

	}
}
