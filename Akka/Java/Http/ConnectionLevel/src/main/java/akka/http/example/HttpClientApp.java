package akka.http.example;

import java.util.concurrent.CompletionStage;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.OutgoingConnection;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.ResponseEntity;
import akka.japi.function.Function;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;

public class HttpClientApp {

	ActorSystem system = ActorSystem.create("akka-http");
	private final LoggingAdapter log = Logging.getLogger(system, this);

	// before this RUn HttpMarshaller project
	
	public void getConnection() {

		ActorMaterializer materializer = ActorMaterializer.create(system);

		Flow<HttpRequest, HttpResponse, CompletionStage<OutgoingConnection>> connectionFlow = Http
					.get(system).outgoingConnection(
						ConnectHttp.toHost("localhost", 8080));

        final CompletionStage<HttpResponse> responseFuture =
        		Source.single(HttpRequest.create("/emp/0"))
        	   .via(connectionFlow).runWith(Sink.<HttpResponse>head(), materializer);
        
       responseFuture.whenComplete((i,j)->{
        	ResponseEntity entity =  i.entity();
        	Source<ByteString, Object> res = entity.getDataBytes();
        	res.map(byteString -> {log.debug(byteString.decodeString("utf8"));return byteString;})
        	.runWith(Sink.head(), materializer);
        });        
        
        // .mapAsync(1, response -> Unmarshaller (response.entity()).to(null,system.dispatcher(),materializer)) ;

	}

	public static void main(String[] args) {
		HttpClientApp app = new HttpClientApp();
		app.getConnection();
	}
}
