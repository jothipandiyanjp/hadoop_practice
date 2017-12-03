package akka.http.websocket.example;

import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.model.ws.WebSocket;
import akka.japi.Function;
import akka.japi.JavaPartialFunction;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;

public class WebsocketExample {


    public static TextMessage handleTextMessage(TextMessage msg) {
   	/*
   	 * The notion of a "strict" message to represent cases 
    	where a whole message was received in one go.
*/
    	  if (msg.isStrict()){
              return TextMessage.create("Hello "+msg.getStrictText());
    	  }else{
    		  return TextMessage.create(Source.single("Hello ").concat(msg.getStreamedText()));
    	  } }

	
	
    public static Flow<Message, Message, NotUsed> greeter() {
    	return 
                Flow.<Message>create()
                	.collect(new JavaPartialFunction<Message, Message>() {
                		@Override
                		public Message apply(Message message, boolean isCheck)
                				throws Exception {
                			if(isCheck){
                                if (message.isText()) 
                                	return null;
                                else
                                	throw noMatch();
                			}else
                				return handleTextMessage(message.asTextMessage());                			
                		}
					});
    }

	
    public static HttpResponse handleRequest(HttpRequest request) {
        if (request.getUri().path().equals("/greeter")){
            return WebSocket.handleWebSocketRequestWith(request, greeter());
        }else{
            return HttpResponse.create().withStatus(404);
        }
    }

	
	public void example(){
        ActorSystem system = ActorSystem.create("WebSocketSystem");
            final Materializer materializer = ActorMaterializer.create(system);
            CompletionStage<ServerBinding> serverBindingFuture = Http.get(system).bindAndHandleSync(
                    							new Function<HttpRequest, HttpResponse>() {
													
													public HttpResponse apply(HttpRequest request) throws Exception {
														
														return handleRequest(request);
													}
												},ConnectHttp.toHost("localhost", 8080), materializer);
        	}
	
	public static void main(String[] args) {
		WebsocketExample socket =  new WebsocketExample();
		socket.example();
	}
	
	
}
