package samza.yahoo.finance.task;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import samza.yahoo.finance.alert.SendMail;
import samza.yahoo.finance.pojo.Fields;
import samza.yahoo.finance.pojo.Resources;
import samza.yahoo.finance.system.YahooFinanceSystemFactory;
import samza.yahoo.finance.util.JsonConverter;

public class YahooFinanceTaskWithKV implements StreamTask, WindowableTask, InitableTask {

	private Logger log = LoggerFactory.getLogger(YahooFinanceSystemFactory.class);

	private HashMap<String, Fields> originalData = new HashMap<String, Fields>();

	private KeyValueStore<String, Double> store;

	private final SystemStream STOCK_OUTPUT_STREAM = new SystemStream("kafka","raw-data");

	ObjectMapper mapper = new ObjectMapper();
	JsonConverter jsonconv=new JsonConverter();
	@Override
	public void init(Config config, TaskContext context) throws Exception {
		this.store = (KeyValueStore<String, Double>) context.getStore("yahoo-finance-stats");
	}

	@Override
	public void process(IncomingMessageEnvelope envelope,MessageCollector collector, TaskCoordinator coordinator) throws Exception {

		List<Resources> list = jsonconv.fromJson((String) envelope.getMessage());
		
		list.stream().forEach(
				(symbol) -> {
					Fields fields = symbol.getResource().getFields();
					
					originalData.put(fields.getSymbol(), fields);

					Optional<Double> option = Optional.ofNullable(store.get(fields.getSymbol()));
					
					if (!option.isPresent()) 	
						store.put(fields.getSymbol(),option.orElse(fields.getChange()));
			
				});

		collector.send(new OutgoingMessageEnvelope(STOCK_OUTPUT_STREAM,envelope.getMessage()));
	}

	@Override
	public void window(MessageCollector collector, TaskCoordinator coordinator)
			throws Exception {

		
		Function<Entry<String, Fields> , Fields> func1 =map -> originalData.get(map.getKey());		
		
		Function<Entry<String, Fields> , Double> func = (map) -> func1.apply(map).getChange();
		
		Predicate<Entry<String, Fields>> predicate = (symbol) -> func.apply(symbol) != store.get(symbol.getKey());

		originalData
				.entrySet()
				.parallelStream()
				.filter(predicate)
				.forEach(
						(symbol) -> {

							double d1 = store.get(symbol.getKey());
							double d2 = func.apply(symbol);

							String message = (d1 > d2) ? " decreased": " increased";
							
							message += " from " + d1 + " to " + d2;

							new Thread(new SendMail(symbol.getKey(), message,func1.apply(symbol))).start();

							store.put(symbol.getKey(),func.apply(symbol));

						});
	}

}
