package com.bizruntime.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class SimpleHighLevelConsumer  extends Thread{

	private static Logger LOGGER=Logger.getLogger(SimpleHighLevelConsumer.class);
	private final ConsumerConnector consumer;
	private final String topic;

	public SimpleHighLevelConsumer(	String topic) {
		InputStream in = SimpleHighLevelConsumer.class.getClassLoader()
				.getResourceAsStream("kafkaConsumer.properties");
		
		Properties props = new Properties();
		try {
			props.load(in);
		} catch (IOException e) {
			LOGGER.error("While loading property file exception occured  in Consumer Class: "+e.getMessage());
		}
	
		consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
		this.topic = topic;
	}

	public void testConsumer() {
		
		Map<String, Integer> topicCount = new HashMap<String, Integer>();
		topicCount.put(topic, new Integer(1));
		
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams = consumer
				.createMessageStreams(topicCount);
		
		List<KafkaStream<byte[], byte[]>> streams = consumerStreams.get(topic);
		
		for (final KafkaStream stream : streams) {
			ConsumerIterator<byte[], byte[]> consumerIte = stream.iterator();
			while (consumerIte.hasNext())
				LOGGER.info("Messages from producer "+ new String(consumerIte.next().message()));
		}
		if (consumer != null)
			consumer.shutdown();
	}

	public  void run() {
		SimpleHighLevelConsumer consumer = this;
		consumer.testConsumer();		
	}

}
