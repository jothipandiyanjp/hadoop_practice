package com.bizruntime.samza;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

public class OpenWeatherExample implements StreamTask,InitableTask{

		private  KeyValueStore<String,String> kv;
		
		
		@SuppressWarnings("unchecked")
		@Override
		public void init(Config config, TaskContext context) throws Exception {			
			kv=(KeyValueStore<String, String>) context.getStore("open-weather-store");
			
		}
		@Override
		public void process(IncomingMessageEnvelope envelope,
				MessageCollector collector, TaskCoordinator coordinator)
				throws Exception {
				
		
		}
}
