package com.bizruntime.samza;

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

public class PartitionExample implements StreamTask,InitableTask{
	
	private KeyValueStore<String, Integer> store;

	
	
	@Override
	public void init(Config config, TaskContext context) throws Exception {
			context.getStore("");
	}
	
	@Override
	public void process(IncomingMessageEnvelope envelope,
			MessageCollector collector, TaskCoordinator coordinator)
			throws Exception {
		
		String message=(String) envelope.getMessage();

		//		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka","kafka-op"), partitionKey, , envelope.getMessage()));
		
	}
	
}
