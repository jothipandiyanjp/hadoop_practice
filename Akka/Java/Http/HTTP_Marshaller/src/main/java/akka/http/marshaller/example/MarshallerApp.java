package akka.http.marshaller.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import akka.actor.ActorSystem;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.Handler1;
import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.HttpService;
import akka.http.javadsl.server.RequestVal;
import akka.http.javadsl.server.RequestVals;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.directives.BasicDirectives;
import akka.http.javadsl.server.directives.BasicDirectivesBase;
import akka.http.javadsl.server.values.PathMatcher;
import akka.http.javadsl.server.values.PathMatchers;

import static akka.http.javadsl.server.Directives.*;

public class MarshallerApp {

	
	 public static PathMatcher<Integer> empId = PathMatchers.intValue();
	 // json to emp
	 public static RequestVal<Employee> empEntity = RequestVals.entityAs(Jackson.jsonAs(Employee.class));		

	 public  Route appRoute(final Map<Integer, Employee> employees) {
		    EmployeeHelper controller = new EmployeeHelper(employees);
		    final RequestVal<Employee> existingEmployee = RequestVals.lookupInMap(empId, Employee.class, employees);
		    Handler1<Employee> putEmpHandler = (ctx, theEmp) -> {
		        employees.put(theEmp.getId(), theEmp);
		        // emp to json
		        return ctx.completeAs(Jackson.json(), theEmp);
		    };
		  return route(
			            path("emp", empId).route(
			            		// http://localhost:8080/emp/0
			            		// Marshaller
			                    get(extractAndComplete(Jackson.<Employee>json(), existingEmployee))	,			                    
			                    // Unmarshaller
			                    put(handleWith1(empEntity, putEmpHandler)),
			                    delete(handleReflectively(controller, "deletePet", empId))
			             )
				  );
	  }

	
	public void marshaller(){
	    Map<Integer, Employee> employees = new ConcurrentHashMap();
	    Employee jp = new Employee(0, "JP");
	    Employee naren = new Employee(1, "NA");
	    employees.put(0, jp);
	    employees.put(1, naren);
	    
	    
	    ActorSystem system = ActorSystem.create("AkkaMarshaller");
	    HttpService.bindRoute("localhost", 8080, appRoute(employees), system);
	    
	}
	public static void main(String[] args) {
		MarshallerApp app = new MarshallerApp();
		app.marshaller();
	}
}
