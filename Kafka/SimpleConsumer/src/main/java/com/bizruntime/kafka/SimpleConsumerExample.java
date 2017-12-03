package com.bizruntime.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.api.PartitionMetadata;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class SimpleConsumerExample {
	
	private ExecutorService executor;
	private final ConsumerConnector consumer;
	private final String topic;

	public SimpleConsumerExample( String groupId, String topic) {
		InputStream in = SimpleConsumerExample.class.getClassLoader()
				.getResourceAsStream("kafka.properties");
		
		Properties props = new Properties();
		try {
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*	props.put("zookeeper.connect", zookeeper);
		props.put("group.id", groupId);
		props.put("zookeeper.session.timeout.ms", "500");
		props.put("zookeeper.sync.time.ms", "250");
		props.put("auto.commit.interval.ms", "1000");*/

		
		consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(
				props));
		this.topic = topic;
	}

	public void testConsumer(int threadCount) {
		
		Map<String, Integer> topicCount = new HashMap<String, Integer>();
		topicCount.put(topic, new Integer(threadCount));
		
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams = consumer
				.createMessageStreams(topicCount);
		
		List<KafkaStream<byte[], byte[]>> streams = consumerStreams.get(topic);
		
		executor = Executors.newFixedThreadPool(threadCount);
		
		int threadNumber = 0;
		
		for (final KafkaStream stream : streams) {
		
			ConsumerIterator<byte[], byte[]> consumerIte = stream.iterator();
			threadNumber++;
			
		while (consumerIte.hasNext())
				System.out.println("Message from thread :: " + threadNumber
						+ "" + new String(consumerIte.next().message()));
		}
		if (consumer != null)
			consumer.shutdown();
		
		if (executor != null)
			executor.shutdown();
	}

	public static void main(String[] args) {
	//	String topic = args[0];
		//int threadCount = Integer.parseInt(args[1]);
		SimpleConsumerExample simpleConsumer = new SimpleConsumerExample("testgroup", "test");
		simpleConsumer.testConsumer(2);
	}

}