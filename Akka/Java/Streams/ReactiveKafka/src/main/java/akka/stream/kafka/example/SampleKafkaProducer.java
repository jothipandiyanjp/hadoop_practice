package akka.stream.kafka.example;

import java.util.concurrent.CompletionStage;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;

public class SampleKafkaProducer {

	final String URL="http://finance.yahoo.com/webservice/v1/symbols/INFY.NS/quote?format=json&view=detail";

	public void source(){
		final ActorSystem system = ActorSystem.create("akka");
		final ActorMaterializer materializer = ActorMaterializer.create(system);
		HttpRequest request = HttpRequest.create(URL);
		CompletionStage<HttpResponse>  response = Http.get(system).singleRequest(request, materializer);
		
	}
}
