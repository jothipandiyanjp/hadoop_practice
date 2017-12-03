package samza.yahoo.finance.task;


import java.util.ArrayList;
import java.util.Arrays;
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



public class NonITStockTaskCompanyInfo implements StreamTask ,InitableTask{

	private Logger log=LoggerFactory.getLogger(NonITStockTaskCompanyInfo.class);



	private  List<String> companies;

	ObjectMapper mapper=new ObjectMapper();			

	
	@Override
	public void init(Config config, TaskContext context) throws Exception {
		this.companies=Arrays.asList((config.get("systems."+"kafka"+".companies").split(",")));
	}
	public void process(IncomingMessageEnvelope envelope,
			MessageCollector collector, TaskCoordinator coordinator)
			throws Exception {
		
		List<Resources> list=mapper.readValue(envelope.getMessage().toString(), new TypeReference<ArrayList<Resources>>() {});
		
		companies.stream()
				.forEach((company)->{
					List<Resources> l=list.stream()
											.filter((symbol)->symbol.getResource()
													  				.getFields()
													  				.getSymbol()
													  				.equals(company))
											.collect(Collectors.toList());
		
			try {
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka",company+"-Stock"),null,mapper.writeValueAsString(l)));
				log.debug("List  based on company-->"+ company+" ->"+mapper.writeValueAsString(l));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			);
	}
	
	
	
	
}


