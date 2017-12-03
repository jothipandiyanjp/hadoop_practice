package akka.http.example;

import java.security.NoSuchAlgorithmException;
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
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpMethods;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Uri;
import akka.japi.Function;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class HttpsExample {
	ActorSystem system = ActorSystem.create("Akka-Http");
	private LoggingAdapter log = Logging.getLogger(system, this);	

	public void sample() throws NoSuchAlgorithmException{	
		Materializer materializer = ActorMaterializer.create(system);

		final Http http = Http.get(system);


		HttpConnectionContext.https(SSLContext.getDefault());

		final HttpsConnectionContext httpsContext = ConnectionContext.https(SSLContext.getDefault());

	    
		final Function<HttpRequest, HttpResponse> requestHandler = new Function<HttpRequest, HttpResponse>() {
			@Override
			public HttpResponse apply(HttpRequest request) throws Exception {
				Uri uri =request.getUri();
				if(request.method() == HttpMethods.GET){
					return HttpResponse.create()
									.withEntity(ContentTypes.TEXT_HTML_UTF8,
											"<html><body><b>Hello world!</b></body></html>");
				}else
					return null;
			}
		};
		final akka.japi.function.Function<HttpRequest, HttpResponse> requestHandler1 = new akka.japi.function.Function<HttpRequest, HttpResponse>() {
			@Override
			public HttpResponse apply(HttpRequest request) throws Exception {
				Uri uri =request.getUri();
				if(request.method() == HttpMethods.GET){
					return HttpResponse.create()
									.withEntity(ContentTypes.TEXT_HTML_UTF8,
											"<html><body><b>Hello world!</b></body></html>");
				}else
					return null;
			}
		};
		

		http.bindAndHandleSync(requestHandler, ConnectHttp.toHostHttps("localhost", 8081).withCustomHttpsContext(httpsContext),materializer);


	}

	public static void main( String[] args ) throws NoSuchAlgorithmException
    {
		HttpsExample app =new HttpsExample();
    	app.sample();
    }

}
