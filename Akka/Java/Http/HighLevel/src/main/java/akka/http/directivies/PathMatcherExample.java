package akka.http.directivies;

import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Handler1;
import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.RequestVal;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.values.Parameters;
import akka.http.javadsl.server.values.PathMatcher;
import akka.http.javadsl.server.values.PathMatchers;

public class PathMatcherExample extends HttpApp {

    private RequestVal<String> name = Parameters.stringValue("name").withDefault("Mister X");
    private RequestVal<String> name1 = Parameters.stringValue("name").withDefault("Miss Y");

	@Override
	public Route createRoute() {
		Route helloRoute = handleWith1(name, (ctx , name) -> ctx.complete("Hello "+ name));
		Route helloRoute2 = handleWith2(name,name1, (ctx , name1,name2) -> ctx.complete("Hello "+ name1+" ,  "+ name2));
		Handler1<Integer> completeWithUserId =   (ctx, userId) -> ctx.complete("Hello user " + userId);
		PathMatcher<Integer> userId = PathMatchers.intValue();

		return route(
				
				get(pathSingleSlash().route(complete(ContentTypes.TEXT_HTML_UTF8, 
									"<html><body>Hello world!</body></html>")),
					path("hello").route(helloRoute),				
					
					path(PathMatchers.segment("greet"),"all").route(helloRoute2),									
					
					path("user").route(
							        completeWithStatus(StatusCodes.OK)
							),
					pathPrefix("admin","user").route(
							path(userId).route(
							        handleWith1(userId, completeWithUserId)
									)
							)
				));
		
	}
	
	public void bindToHost(){
        ActorSystem system = ActorSystem.create("akka");
        this.bindRoute("localhost", 8080, system); 
	}
	
	public static void main(String[] args) {
		PathMatcherExample http = new PathMatcherExample();
		http.bindToHost();
	}

}

