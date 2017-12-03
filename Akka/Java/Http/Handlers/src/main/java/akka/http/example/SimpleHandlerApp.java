package akka.http.example;

import akka.actor.ActorSystem;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Handler;
import akka.http.javadsl.server.HttpService;
import akka.http.javadsl.server.RequestContext;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.RouteResult;

public class SimpleHandlerApp extends AllDirectives {

	Handler handlerString = new Handler() {

		private static final long serialVersionUID = 1L;

		public RouteResult apply(RequestContext ctx) {
			return ctx.complete(String.format("This was a %s request to %s",
					ctx.request().method().value(), ctx.request().getUri()));
		}
	};

	Route createRoute() {

		return route(get(handleWith(handlerString)));
	}

	public void multipleHandlers() {
		ActorSystem system = ActorSystem.create("akka");
		HttpService.bindRoute("localhost", 8080, createRoute(), system);
	}

	public static void main(String[] args) {
		SimpleHandlerApp app = new SimpleHandlerApp();
		app.multipleHandlers();
	}
}
