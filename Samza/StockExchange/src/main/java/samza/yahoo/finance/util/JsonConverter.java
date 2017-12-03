package samza.yahoo.finance.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import samza.yahoo.finance.pojo.Resources;

public class JsonConverter {

	private ObjectMapper mapper=new ObjectMapper();
	
	public List<Resources> fromJson(String json) throws JsonParseException, JsonMappingException, IOException{
		return mapper.readValue(json,new TypeReference<ArrayList<Resources>>() {});
	}

	public String toJson(List<Resources> list) throws JsonGenerationException, JsonMappingException, IOException {
		return mapper.writeValueAsString(list);
	}
	
}
