package akka.stream.example;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedQueue;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Framing;
import akka.stream.javadsl.FramingTruncation;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Tcp;
import akka.stream.javadsl.Tcp.IncomingConnection;
import akka.stream.javadsl.Tcp.OutgoingConnection;
import akka.stream.javadsl.Tcp.ServerBinding;
import akka.util.ByteString;

public class ClientServerWithAvoidingDeadlocksApp {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer mat = ActorMaterializer.create(system);

	public void server() {

		final Source<IncomingConnection, CompletionStage<ServerBinding>> connections = Tcp
				.get(system).bind("127.0.0.1", 8889);

		connections
				.runForeach(
						connection -> {
							final Flow<String, String, NotUsed> commandParser = Flow
									.<String> create()
									.takeWhile(elem -> !elem.equals("BYE"))
									.map(elem -> elem + "!");

							final String welcomeMsg = "Welcome to: "
									+ connection.localAddress() + " you are: "
									+ connection.remoteAddress() + "!";

							final Source<String, NotUsed> welcome = Source
									.single(welcomeMsg);

							final Flow<ByteString, ByteString, NotUsed> serverLogic = Flow
									.of(ByteString.class)
									.via(Framing.delimiter(
											ByteString.fromString("\n"), 20,
											FramingTruncation.DISALLOW))
									.map(ByteString::utf8String)
									.map(command -> {
										log.debug(command);
										return command;
									}).via(commandParser).merge(welcome)
									.map(s -> s + "\n")
									.map(ByteString::fromString);

							connection.handleWith(serverLogic, mat);

						}, mat);

	}

	public void client() {
		final Flow<ByteString, ByteString, CompletionStage<OutgoingConnection>> connection = Tcp
				.get(system).outgoingConnection("127.0.0.1", 8889);
		final Flow<String, ByteString, NotUsed> replParser = Flow
				.<String> create().takeWhile(elem -> !elem.equals("quit"))
				.concat(Source.single("BYE"))
				.map(elem -> ByteString.fromString(elem + "\n"));

		final Flow<ByteString, ByteString, NotUsed> repl = Flow
				.of(ByteString.class)
				.via(Framing.delimiter(ByteString.fromString("\n"), 256,
						FramingTruncation.DISALLOW))
				.map(ByteString::utf8String).map(text -> {
					log.debug("Server: " + text);
					return "next";
				}).map(elem -> readLine()).via(replParser);

		connection.join(repl).run(mat);

	}

	public final ConcurrentLinkedQueue<String> input = new ConcurrentLinkedQueue<String>();
	{
		input.add("Good Morning");
		input.add("What a lovely day");
	}

	public String readLine() {
		String s = input.poll();
		return (s == null ? "quit" : s);
	}

	public static void main(String[] args) {
		ClientServerWithAvoidingDeadlocksApp app = new ClientServerWithAvoidingDeadlocksApp();
		app.server();
		app.client();
	}
}
