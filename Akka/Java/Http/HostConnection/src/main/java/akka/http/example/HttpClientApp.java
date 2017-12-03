package akka.http.example;

import java.util.concurrent.CompletionStage;

import scala.util.Try;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.HostConnectionPool;
import akka.http.javadsl.Http;
import akka.http.javadsl.OutgoingConnection;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class HttpClientApp {
	ActorSystem system = ActorSystem.create("akka-http");
	private final LoggingAdapter log = Logging.getLogger(system, this);

	public void getConnection() {
		
		ActorMaterializer materializer = ActorMaterializer.create(system);

		Flow<Pair<HttpRequest, Integer>, Pair<Try<HttpResponse>, Integer>, HostConnectionPool> poolClientFlow = Http
				.get(system).cachedHostConnectionPool(ConnectHttp.toHost("localhost", 8080), materializer);

		CompletionStage<Pair<Try<HttpResponse>, Integer>> responseFuture = Source
				.single(Pair.create(HttpRequest.create("/emp/0"), 1025))
				.via(poolClientFlow)
				.runWith(Sink.<Pair<Try<HttpResponse>, Integer>> head(), materializer);

		responseFuture.whenComplete((response, throwable) -> log
				.debug("Response from server \n" + response));
	}

	public static void main(String[] args) {
		HttpClientApp app = new HttpClientApp();
		app.getConnection();
	}

}
