package com.bizruntime.samza;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

public class MyStatefulTask implements StreamTask, InitableTask {
	private KeyValueStore<String, String> store;

	public void init(Config config, TaskContext context) {
		this.store = (KeyValueStore<String, String>) context.getStore("my-store");
		
	}

	public void process(IncomingMessageEnvelope envelope,
			MessageCollector collector, TaskCoordinator coordinator) {
		store.put((String) envelope.getKey(), (String) envelope.getMessage());
	}
	
	
}