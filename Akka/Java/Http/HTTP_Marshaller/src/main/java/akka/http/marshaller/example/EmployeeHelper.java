package akka.http.marshaller.example;

import java.util.Map;

import akka.http.javadsl.server.RequestContext;
import akka.http.javadsl.server.RouteResult;

public class EmployeeHelper {
	private Map<Integer, Employee> dataStore;

	public EmployeeHelper(Map<Integer, Employee> dataStore) {
		super();
		this.dataStore = dataStore;
	}

	public RouteResult deletePet(RequestContext ctx, int empId) {
		dataStore.remove(empId);
		return ctx.completeWithStatus(200);
	}

}
