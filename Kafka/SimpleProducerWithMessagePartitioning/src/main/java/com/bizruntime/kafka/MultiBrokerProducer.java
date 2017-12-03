package com.bizruntime.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
public class MultiBrokerProducer {

	private static Producer<String,String> producer;
	private final Properties props = new Properties();

	public MultiBrokerProducer() {
		InputStream in = MultiBrokerProducer.class.getClassLoader()
				.getResourceAsStream("kafka.properties");
		try {
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String brokerlist= props.get("server1")+","+props.get("server2");
		props.put("metadata.broker.list",brokerlist);
		props.put("serializer.class", props.get("serializerclass"));
		props.put("partitioner.class", props.get("partitionerclass"));
		props.put("request.required.acks", props.get("requiredacks"));
		/*
		props.put("metadata.broker.list","localhost:9092,localhost:9093");
				props.put("serializer.class","kafka.serializer.StringEncoder");
				props.put("partitioner.class", "com.bizruntime.kafka.SimplePartitioner");
				props.put("request.required.acks", "1");
		*/
		producer = new Producer<String,String>(new ProducerConfig(props));

	}

	public static void main(String[] args) {
		
		MultiBrokerProducer sp = new MultiBrokerProducer();
		Random rnd = new Random();
		String topic = (String) args[0];
		
		for (long messCount = 0; messCount < 10; messCount++) {
			Integer key =rnd.nextInt(255);
			String msg = "This message is for key - " + key;
			String key1=""+key;
			KeyedMessage<String, String> data1 = new KeyedMessage<String, String>(topic, key1, msg);
			producer.send(data1);
			}
		
		producer.close();
		
		
	}
}
