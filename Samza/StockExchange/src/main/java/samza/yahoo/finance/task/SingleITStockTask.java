package samza.yahoo.finance.task;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samza.yahoo.finance.pojo.Resources;



public class SingleITStockTask implements StreamTask ,InitableTask{

	private Logger log=LoggerFactory.getLogger(SingleITStockTask.class);


	private final SystemStream STOCK_OUTPUT_STREAM = new SystemStream("kafka","Google-stock1");
	
	private  String systemName;
	private  String company;

	ObjectMapper mapper=new ObjectMapper();			

	
	@Override
	public void init(Config config, TaskContext context) throws Exception {
		systemName = context.getSystemStreamPartitions().iterator().next().getSystem();
		this.company=config.get("systems."+"kafka"+".company");
		log.debug("System name "+systemName);
		log.debug(config.get("systems."+systemName+".company"));
	}
	public void process(IncomingMessageEnvelope envelope,
			MessageCollector collector, TaskCoordinator coordinator)
			throws Exception {
		log.debug("Envelope key "+envelope.getKey());
		log.debug("Envelope "+envelope);
		List<Resources> list=mapper.readValue(envelope.getMessage().toString(), new TypeReference<ArrayList<Resources>>() {});
		
		List<Resources> l=list.stream()
			.filter((symbol)-> symbol.getResource().getFields().getSymbol().equals(company))
			.collect(Collectors.toList());
		
			collector.send(new OutgoingMessageEnvelope(STOCK_OUTPUT_STREAM,null,mapper.writeValueAsString(l)));
	}
	
	
	
	
}


