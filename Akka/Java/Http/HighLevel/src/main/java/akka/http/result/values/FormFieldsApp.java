package akka.http.result.values;

import akka.actor.ActorSystem;
import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.values.FormField;
import akka.http.javadsl.server.values.FormFields;

public class FormFieldsApp extends HttpApp{

	
	@Override
	public Route createRoute() {
		FormField<String> name = FormFields.stringValue("name");
		FormField<Integer> age = FormFields.intValue("age");
		final Route route =
				  route(
				    handleWith2(name, age, (ctx, n, a) ->
				    {
				    	// POST request
				      return ctx.complete(String.format("Name: %s, age: %d", n, a));
	}
				    )
				  );
		return route(path("form").route(route));
	}
	public void usingFormFields(){
		
			ActorSystem system = ActorSystem.create("akka");
	        this.bindRoute("localhost", 8080, system);
	     
	}
	public static void main(String[] args) {
		FormFieldsApp app = new FormFieldsApp();
		app.usingFormFields();
		
	}
}
