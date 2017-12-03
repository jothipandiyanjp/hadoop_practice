package com.bizruntime.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class SimpleHighLevelConsumer {

	private final ConsumerConnector consumer;
	private final String topic;

	public SimpleHighLevelConsumer(String groupId,
			String topic) {
		InputStream in = SimpleHighLevelConsumer.class.getClassLoader()
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
				System.out.println("Message from Single Topic :: "
						+ new String(consumerIte.next().message()));
		}
		if (consumer != null)
			consumer.shutdown();
	}

	public static void main(String[] args) {
		String topic = args[0];
		SimpleHighLevelConsumer consumer = new SimpleHighLevelConsumer( "testgroup", topic);
		consumer.testConsumer();		
	}

}
