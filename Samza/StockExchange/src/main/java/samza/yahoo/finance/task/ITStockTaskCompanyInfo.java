package samza.yahoo.finance.task;



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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samza.yahoo.finance.pojo.Resources;
import samza.yahoo.finance.util.JsonConverter;



public class ITStockTaskCompanyInfo implements StreamTask ,InitableTask{

	private Logger log=LoggerFactory.getLogger(ITStockTaskCompanyInfo.class);

	private  List<String> companies;

	ObjectMapper mapper=new ObjectMapper();			
JsonConverter conv=new JsonConverter();
	
	@Override
	public void init(Config config, TaskContext context) throws Exception {
		this.companies=Arrays.asList((config.get("systems."+"kafka"+".companies").split(",")));
	}
	public void process(IncomingMessageEnvelope envelope,
			MessageCollector collector, TaskCoordinator coordinator)
			throws Exception {
		
		List<Resources> list=conv.fromJson((String)envelope.getMessage());
		
		companies.stream()
				.forEach((company)->{
					List<Resources> resourceList=list.stream()
											.filter((symbol)->symbol.getResource()
													  				.getFields()
													  				.getSymbol()
													  				.equals(company))
											.collect(Collectors.toList());
		
			try {
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka",company+"-Stock"),null,conv.toJson(resourceList)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			);
	}
	
	
	
	
}


