package samza.yahoo.finance.pojo;

import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Results {

	private ArrayList<FinanceWebSeriveURL> results;

	public ArrayList<FinanceWebSeriveURL> getResults() {
		return results;
	}

	public void setResults(ArrayList<FinanceWebSeriveURL> results) {
		this.results = results;
	}

	
}
