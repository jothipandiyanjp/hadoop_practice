package samza.yahoo.finance.pojo;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Resources {

	
	private Resource resource;

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	@Override
	public String toString() {
		return "Resources [resource=" + resource + "]";
	}
	
	

	
}
