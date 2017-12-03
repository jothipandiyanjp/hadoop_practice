package akka.http.result.values;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import akka.actor.ActorSystem;
import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.values.BasicCredentials;
import akka.http.javadsl.server.values.FormField;
import akka.http.javadsl.server.values.FormFields;
import akka.http.javadsl.server.values.HttpBasicAuthenticator;

public class HttpBasicAuth extends HttpApp{

	
	final HttpBasicAuthenticator<String> authentication = new HttpBasicAuthenticator<String>("authenticator") {
	    private final String password = "password";

		@Override
		public CompletionStage<Optional<String>> authenticate(BasicCredentials credentials) {
			if(credentials.available() && credentials.verify(password)){
				return authenticateAs(credentials.identifier());
			}else
				return refuseAccess();
		}
	};
	@Override
	public Route createRoute() {
			
		final Route route = authentication.route(
				    handleWith1(authentication, (ctx, user) ->
	                    ctx.complete("Hello " + user + "!")
				    )
				  );
		return route(path("basic").route(route));
	}
	public void usingFormFields(){
		
			ActorSystem system = ActorSystem.create("akka");
	        this.bindRoute("localhost", 8080, system);
	     
	}
	public static void main(String[] args) {
		HttpBasicAuth app = new HttpBasicAuth();
		app.usingFormFields();
		
	}
}
