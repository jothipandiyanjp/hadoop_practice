package com.bizruntime.kafka;

import java.io.IOException;

import java.io.InputStream;
import java.util.Properties;

import kafka.admin.AdminUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.utils.ZKStringSerializer;

public class SimpleProducer {
	private static Producer<String, String> producer;
	private final Properties props = new Properties();
	public SimpleProducer() {
		InputStream in = SimpleProducer.class.getClassLoader()
				.getResourceAsStream("kafka.properties");
		try {
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		props.put("metadata.broker.list", props.get("brokerlist"));
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("request.required.acks", props.get("requiredacks"));
		props.put("producer.type","async");
		props.put("batch.num.messages", "200");
		props.put("queue.buffering.max.ms","5000");
		
		producer = new Producer<String, String>(new ProducerConfig(props));

	}

	public static void main(String[] args) {
		
		SimpleProducer sp = new SimpleProducer();
		String topic = "test";
		String messageStr = "Test";
		KeyedMessage<String, String> data = new KeyedMessage<String, String>(
				topic, messageStr);
		
		producer.send(data);
		producer.close();
		
	}
}
