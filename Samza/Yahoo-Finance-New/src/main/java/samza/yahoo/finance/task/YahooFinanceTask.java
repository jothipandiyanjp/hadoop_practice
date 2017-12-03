package samza.yahoo.finance.task;

import java.util.ArrayList;

import java.util.HashMap;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samza.yahoo.finance.alert.SendMail;
import samza.yahoo.finance.pojo.Results;
import samza.yahoo.finance.pojo.YahooFinance;
import samza.yahoo.finance.system.YahooFinanceSystemFactory;

public class YahooFinanceTask implements StreamTask, InitableTask,WindowableTask {
	
	private Logger log = LoggerFactory.getLogger(YahooFinanceSystemFactory.class);

	private HashMap<String, Double> originalData = new HashMap<String, Double>();
	private HashMap<String, Double> updatedByWindow = new HashMap<String, Double>();

	
	private final SystemStream ALL_STOCK_OUTPUT_STREAM = new SystemStream("kafka","stock-based-for-all-symbol");
	private final SystemStream UPDATED_STOCK_OUTPUT_STREAM = new SystemStream("kafka","stock-based-on-time");
	
	public void init(Config config, TaskContext context) throws Exception {

			
	}
	
	
	private ObjectMapper mapper = new ObjectMapper();
	ArrayList<YahooFinance> list =null;
	
	public void process(IncomingMessageEnvelope envelope,
			MessageCollector collector, TaskCoordinator coordinator)
			throws Exception {

		HashMap<String, Results> map = mapper.readValue(envelope.getMessage()
				.toString(), new TypeReference<HashMap<String, Results>>() {
		});
		list = map.get("query").getResults().get("quote");

		list.parallelStream().forEach(
				symbol -> {
				
					 originalData.put(symbol.getSymbol(),Double.parseDouble(symbol.getChange()));});
		
		log.debug("IncomingMessageEnvelope value --> "+envelope.getMessage());

		map = null;
		list = null;

	}

	public void window(MessageCollector collector, TaskCoordinator coordinator)
			throws Exception {

		if (updatedByWindow.isEmpty()) {
			originalData
					.entrySet()
					.parallelStream()
					.forEach(
							(symbol) -> updatedByWindow.put(symbol.getKey(),symbol.getValue()));
		}

		updatedByWindow.entrySet()
						.stream()
						.filter((symbol) -> {
								return originalData.get(symbol.getKey()).doubleValue() != (symbol
										.getValue()).doubleValue();
								})
						.forEach(
								(symbol) -> {
									new SendMail().createAlert(symbol.getKey(),originalData.get(symbol.getKey()));
									updatedByWindow.put(symbol.getKey(),originalData.get(symbol.getKey()));
								});

		collector.send(new OutgoingMessageEnvelope(UPDATED_STOCK_OUTPUT_STREAM, mapper.writeValueAsString(updatedByWindow)));
	}
}
