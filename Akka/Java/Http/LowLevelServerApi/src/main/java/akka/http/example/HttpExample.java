package akka.http.example;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.net.ssl.SSLContext;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.ConnectionContext;
import akka.http.javadsl.Http;
import akka.http.javadsl.HttpConnectionContext;
import akka.http.javadsl.HttpsConnectionContext;
import akka.http.javadsl.IncomingConnection;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.ContentType;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpMethods;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Uri;
import akka.japi.function.Function;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

/**
 * Hello world!
 *
 */
public class HttpExample 
{
	ActorSystem system = ActorSystem.create("Akka-Http");
	private LoggingAdapter log = Logging.getLogger(system, this);	

	public void sample() throws NoSuchAlgorithmException{	

/*		ActorMaterializer is responsible for creating actors that 
 *      will eventually run the processing flow and it requires ActorSystem 
 *      to be available implicitly.
 */

		
		Materializer materializer = ActorMaterializer.create(system);

		final Http http = Http.get(system);

		Source<IncomingConnection, CompletionStage<ServerBinding>> serverSource =
		  http.bind(ConnectHttp.toHost("localhost", 8080), materializer);



	    
		final Function<HttpRequest, HttpResponse> requestHandler1 = new Function<HttpRequest, HttpResponse>() {
			public HttpResponse apply(HttpRequest request) throws Exception {
				Uri uri =request.getUri();
		        final HttpResponse NOT_FOUND =
		                HttpResponse.create()
		                    .withStatus(404)
		                    .withEntity("Unknown resource!");
		     
				if(request.method() == HttpMethods.GET){
					if(uri.path().equals("/"))
						return HttpResponse.create()
									.withEntity(ContentTypes.TEXT_HTML_UTF8,
											"<html><body><h1>Response from Server using low level api..</h1></body></html>");
					else if(uri.path().equals("/index")){
	                    Optional<String> name = uri.query().get("name");
						return HttpResponse.create().withEntity(ContentTypes.APPLICATION_JSON,
								"{\"name\" : "+name.get()+"}");
					}else
						return NOT_FOUND;
				}else
					return NOT_FOUND;
			}
		};
		
		CompletionStage<ServerBinding> serverBindingFuture = serverSource.to(Sink.foreach(connection -> {
			log.debug("Connection received from "+((IncomingConnection)connection).remoteAddress());
			((IncomingConnection)connection).handleWithSyncHandler(requestHandler1, materializer);
			})).run(materializer);


	}

	public static void main( String[] args ) throws NoSuchAlgorithmException
    {
    	HttpExample app =new HttpExample();
    	app.sample();
    }
}
