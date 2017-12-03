package akka.http.result.values;

import java.util.Optional;

import java.util.concurrent.CompletionStage;
import akka.actor.ActorSystem;
import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.values.OAuth2Authenticator;
import akka.http.javadsl.server.values.OAuth2Credentials;


public class OAuth2App extends HttpApp{

	
	final OAuth2Authenticator<String> authentication = new OAuth2Authenticator<String>("authenticator") {
	    private final String token = "tokentokentoken";

		@Override
		public CompletionStage<Optional<String>> authenticate(OAuth2Credentials credentials) {
			if(credentials.available() && credentials.verify(token)){
				return authenticateAs(credentials.identifier());
			}else
				return refuseAccess();
		}
	};
	
	@Override
	public Route createRoute() {
		final Route route = authentication.route(
				    handleWith1(authentication, (ctx, token) ->
                    ctx.complete("The secret token is: " + token)
				    )
				  );
		return route(path("oauth").route(route));
	}
	public void usingFormFields(){
		
			ActorSystem system = ActorSystem.create("akka");
	        this.bindRoute("localhost", 8080, system);
	     
	}
	public static void main(String[] args) {
		OAuth2App app = new OAuth2App();
		app.usingFormFields();
		
	}


}
