package com.bizruntime.kafka;



import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;


public class SimpleConsumer {
	private static Logger LOGGER=Logger.getLogger(SimpleConsumer.class);

	private  KafkaConsumer<String, String> consumer;
	public SimpleConsumer(String groupId,
			String topic) {
	
		InputStream in = SimpleConsumer.class.getClassLoader()
				.getResourceAsStream("kafka.properties");
		
		Properties props = new Properties();
	     props.put("bootstrap.servers", "localhost:9092");
	     props.put("group.id", "test1");
	     props.put("enable.auto.commit", "true");
	     props.put("auto.commit.interval.ms", "1000");
	     props.put("session.timeout.ms", "30000");
	     props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	     props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	     props.put("heartbeat.interval.ms","250");
	     props.put("session.timeout.ms","9000");
	     
	      consumer = new KafkaConsumer<String, String>(  props);
	     
	}

	public void consume() {
		consumer.subscribe(Arrays.asList("NewProducer"));
	     while (true) {
	         ConsumerRecords<String, String> records = consumer.poll(100);
	         for (ConsumerRecord<String, String> record : records){
	             LOGGER.debug( "offset "+record.offset()+" key "+ record.key()+" value"+ record.value());     
	         }
        }
	 
		
		
	}

	public static void main(String[] args) {
		String topic = "NewProducer1";
		SimpleConsumer consumer = new SimpleConsumer( "testgroup", topic);
		consumer.consume();
	}

}
