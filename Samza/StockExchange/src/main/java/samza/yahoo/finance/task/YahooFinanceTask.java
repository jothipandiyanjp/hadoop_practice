package samza.yahoo.finance.task;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samza.yahoo.finance.alert.SendMail;
import samza.yahoo.finance.pojo.Fields;
import samza.yahoo.finance.pojo.Resources;
import samza.yahoo.finance.system.YahooFinanceSystemFactory;

public class YahooFinanceTask implements StreamTask, WindowableTask {
	
	private Logger log = LoggerFactory.getLogger(YahooFinanceSystemFactory.class);

	// update by process method
	private HashMap<String, Fields> originalData = new HashMap<String, Fields>();

	// update by window method	
	private HashMap<String, Fields> updatedByWindow ;

	private final SystemStream STOCK_OUTPUT_STREAM = new SystemStream("kafka","raw-data");

	ObjectMapper mapper = new ObjectMapper();

	@Override
	public void process(IncomingMessageEnvelope envelope,
			MessageCollector collector, TaskCoordinator coordinator)
			throws Exception {
log.debug("process check partiton "+envelope.getSystemStreamPartition().getPartition().getPartitionId());
		log.debug(envelope.getSystemStreamPartition().getPartition().getPartitionId()+"");
		List<Resources> list = mapper.readValue((String)envelope.getMessage(), new TypeReference<ArrayList<Resources>>() {});
		
		list.stream().forEach((symbol) -> {
			Fields fields = symbol.getResource().getFields();
			originalData.put(fields.getSymbol(), fields);

		collector.send(new OutgoingMessageEnvelope(STOCK_OUTPUT_STREAM,fields.getSymbol().hashCode()%4,fields.getSymbol(),envelope.getMessage()));

		});

	log.debug("envelpoe message"+envelope.getMessage());
	list =null;
	}

	@Override
	public void window(MessageCollector collector, TaskCoordinator coordinator)
			throws Exception {
		
		// Get Double from Field class 
		BiFunction<Map<String, Fields>, Entry<String, Fields>, Double> getDouble = (
				x, y) -> x.get(y.getKey()).getChange();

		// Comparing originalData HashMap's and updateByWindow HashMap's using it's field getChange()
		Predicate<Entry<String, Fields>> predicate = (symbol) ->
														getDouble.apply(originalData, symbol).doubleValue() 
														!= getDouble.apply(updatedByWindow, symbol).doubleValue();

		// Checking HashMap is NUll or Not
			Optional<HashMap<String, Fields>> option = Optional.ofNullable(updatedByWindow);
	
		// If NULL Put data from process to updateByWindow HashMap	
		if(!option.isPresent())
			updatedByWindow = option.orElse(new HashMap<String, Fields>(originalData));

		// IF Hashmap is not null do further operation		
		option.ifPresent((map)->
				map.entrySet()
				.parallelStream()
				.filter(predicate) // Comparing both originalData HashMap's key-value and updateByWindow HashMap's key-value. 
				.forEach(
						(symbol) -> {

							double d1 = getDouble
									.apply(updatedByWindow, symbol);
							double d2 = getDouble.apply(originalData, symbol);
							
							String message = (d1 > d2) ? " decreased"
									: " increased";
							message += " from " + d1 + " to " + d2;

							// Creating thread to send alert 
							new Thread(new SendMail(symbol.getKey(), message,originalData.get(symbol.getKey()))).start();

							// Finally updating value of orginalData to updatedByWindow for further processing.
							updatedByWindow.put(symbol.getKey(),
									originalData.get(symbol.getKey()));

						}));
							
					
		log.debug(updatedByWindow.toString());
		log.debug("End of window");
	}

	}
