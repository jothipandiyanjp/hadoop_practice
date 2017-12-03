package akka.http.directivies;

import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Handler1;
import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.RequestVal;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.directives.MethodDirectives;
import akka.http.javadsl.server.values.Parameters;
import akka.http.javadsl.server.values.PathMatcher;
import akka.http.javadsl.server.values.PathMatchers;

public class MethodDirectivesExample extends HttpApp {

    private RequestVal<String> name = Parameters.stringValue("name").withDefault("Mister X");

	@Override
	public Route createRoute() {
		Route helloRoute = handleWith1(name, (ctx , name) -> ctx.complete("Hello "+ name));
		final Route route = post(complete("This is a POST request."));

		return route(path("hello").route(
				get(complete("This is a POST request.")),
				post(complete("This is a POST request.")),
				delete(complete("This is a DELETE request."))
				));
		
	}
	
	
	public void bindToHost(){
        ActorSystem system = ActorSystem.create("akka");
        this.bindRoute("localhost", 8080, system);
        
	}
	
	public static void main(String[] args) {
		MethodDirectivesExample http = new MethodDirectivesExample();
		http.bindToHost();
	}

}

