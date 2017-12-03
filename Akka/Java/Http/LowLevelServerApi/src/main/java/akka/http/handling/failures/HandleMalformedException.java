package akka.http.handling.failures;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.net.ssl.SSLContext;

import akka.NotUsed;
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
import akka.stream.BindFailedException;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

/**
 * Hello world!
 *
 */
public class HandleMalformedException 
{
	ActorSystem system = ActorSystem.create("Akka-Http");
	private LoggingAdapter log = Logging.getLogger(system, this);	

	public void sample() throws NoSuchAlgorithmException{	
		
		Materializer materializer = ActorMaterializer.create(system);

		final Http http = Http.get(system);

		Source<IncomingConnection, CompletionStage<ServerBinding>> serverSource =
		  http.bind(ConnectHttp.toHost("localhost", 8081), materializer);

	    
		Flow<HttpRequest, HttpRequest, NotUsed> failureDetection = Flow.of(HttpRequest.class)
		        .watchTermination((notUsed, termination) -> {
		        	System.out.println("1");
		        	termination.whenComplete((done, cause) -> {
		        		System.out.println("2");
		                if (cause == null) {
		                	log.error("Request url malfunctioned..Please check the url");
		                	system.terminate();
		                	
		                }
		            });
		            return NotUsed.getInstance();
		        });

		

		Flow<HttpRequest, HttpResponse, NotUsed> httpEcho = Flow.of(HttpRequest.class)
			.via(failureDetection).map( new Function<HttpRequest, HttpResponse>() {
			@Override
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
		});
		
		
		CompletionStage<ServerBinding> serverBindingFuture = serverSource.to(Sink.foreach(connection -> {
			log.debug("Connection received from "+((IncomingConnection)connection).remoteAddress());
			((IncomingConnection)connection).handleWith(httpEcho, materializer);
		})).run(materializer);

		
		serverBindingFuture.whenCompleteAsync((binding, failure) -> {
			if(failure instanceof BindFailedException){
				log.error("Binding port error occurred -> "+failure.getMessage());
				system.terminate();
			}
	
		},system.dispatcher());
			
	}

	public static void main( String[] args ) throws NoSuchAlgorithmException
    {
    	HandleMalformedException app =new HandleMalformedException();
    	app.sample();
    }
}
