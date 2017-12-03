package samza.yahoo.finance.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Resource {

	private Fields fields;

	public Fields getFields() {
		return fields;
	}

	public void setFields(Fields fields) {
		this.fields = fields;
	}

	@Override
	public String toString() {
		return "Resource [fields=" + fields + "]";
	}
	
	
}

