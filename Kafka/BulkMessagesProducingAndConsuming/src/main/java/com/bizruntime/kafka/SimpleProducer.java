 package com.bizruntime.kafka;

import java.io.IOException;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class SimpleProducer extends  Thread {
	
	private static Logger LOGGER=Logger.getLogger(SimpleProducer.class);
	private static Producer<String, String> producer;
	private final Properties props = new Properties();

		
	public SimpleProducer() {
		InputStream in = SimpleProducer.class.getClassLoader()
				.getResourceAsStream("kafkaProducer.properties");
		try {
			props.load(in);
		} catch (IOException e) {
			LOGGER.error("While loading property file exception occured  in Producer Class: "+e.getMessage());
		}
		
		producer = new Producer<String, String>(new ProducerConfig(props));
 
	}

	public void run() {

		SimpleProducer sp = this;
		List<KeyedMessage<String, String>> list=new ArrayList<KeyedMessage<String,String>>();
		
		while(true){
			KeyedMessage<String, String> messages=getMessage();
			list.add(messages);
			LOGGER.debug("Messages to be  add in list: "+messages.message());
			producer.send(list);
			
		}
		
	}
	
	public static KeyedMessage<String, String>  getMessage(){
		String message="Randomly Selecting Number --> "+(Math.random()*100);
			KeyedMessage<String, String> data = new KeyedMessage<String, String>("kafka-feed3",message);		
		return data;
	}
}
