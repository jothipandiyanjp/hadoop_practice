package samza.yahoo.finance.task;


import java.util.ArrayList;

import java.util.List;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskCoordinator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samza.yahoo.finance.pojo.Resources;



public class NonITStockTask implements StreamTask {

	private Logger log=LoggerFactory.getLogger(NonITStockTask.class);
	
	ObjectMapper mapper=new ObjectMapper();			

	public void process(IncomingMessageEnvelope envelope,
			MessageCollector collector, TaskCoordinator coordinator)
			throws Exception {
		log.debug("Envelope key "+envelope.getKey());
		log.debug("Envelope "+envelope);
		
		List<Resources> list=mapper.readValue(envelope.getMessage().toString(), new TypeReference<ArrayList<Resources>>() {});

		list.stream().forEach((symbol) -> {		
			collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka",symbol.getResource()
																					  .getFields()
																					  .getSymbol()+"-Stock"),null,null ,symbol.getResource()																					  										.getFields()));
		});
			
	}
			
	
	
	

	
}


