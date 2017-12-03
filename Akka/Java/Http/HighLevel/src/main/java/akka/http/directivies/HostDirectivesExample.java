package akka.http.directivies;

import java.util.regex.Pattern;

import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Handler1;
import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.RequestVal;
import akka.http.javadsl.server.RequestVals;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.directives.MethodDirectives;
import akka.http.javadsl.server.values.Parameters;
import akka.http.javadsl.server.values.PathMatcher;
import akka.http.javadsl.server.values.PathMatchers;

public class HostDirectivesExample extends HttpApp {

    private RequestVal<String> name = Parameters.stringValue("name").withDefault("Mister X");

	@Override
	public Route createRoute() {
		
		//  filtering only using localhost as hostname 
		final Route matchListOfHosts = 	host("localhost", completeWithStatus(StatusCodes.OK));
		
		// filtering based on hostnamelength
		final Route shortOnly = host(hostname -> hostname.length() >= 9, completeWithStatus(StatusCodes.OK));

		// printing hostname
		final RequestVal<String> host = RequestVals.host();
		final Route hostname = handleWith1(host,(ctx, hn) -> ctx.complete("Hostname: " + hn));
	
		// Extracting prefix of hostname
		final RequestVal<String> hostPrefix = RequestVals.matchAndExtractHost(Pattern.compile("local|rest"));
		final Route hostPrefixRoute = handleWith1(hostPrefix,(ctx, prefix) -> ctx.complete("Extracted prefix: " + prefix));

		return route(
				
				path("hello1").route(matchListOfHosts), // use localhost
				path("hello2").route(shortOnly), // Use both 127.0.0.1 & localhost
				path("hello3").route(hostname),
				path("hello4").route(hostPrefixRoute) 
				
				);
		
	}
	
	
	public void bindToHost(){
        ActorSystem system = ActorSystem.create("akka");
        this.bindRoute("localhost", 8080, system);
        
	}
	
	public static void main(String[] args) {
		HostDirectivesExample http = new HostDirectivesExample();
		http.bindToHost();
	}

}

