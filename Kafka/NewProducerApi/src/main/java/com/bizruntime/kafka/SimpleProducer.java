package com.bizruntime.kafka;

import java.util.Properties;


import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

public class SimpleProducer {
	private static Logger LOGGER = Logger.getLogger(SimpleProducer.class);
	private static Producer<String, String> producer;
	private final Properties props = new Properties();

	public SimpleProducer() {
		/*
		 * InputStream in = SimpleProducer.class.getClassLoader()
		 * .getResourceAsStream("kafka.properties"); try { props.load(in); }
		 * catch (IOException e) { e.printStackTrace(); }
		 */
		/*
		 * props.put("bootstrap.servers", "localhost:9092"); props.put("acks",
		 * "all"); props.put("retries", 0); props.put("batch.size", 16384);
		 * props.put("linger.ms", 1); props.put("buffer.memory", 33554432);
		 * props.put("key.serializer",
		 * "org.apache.kafka.common.serialization.StringSerializer");
		 * props.put("value.serializer",
		 * "org.apache.kafka.common.serialization.StringSerializer");
		 * props.put("ssl.key.password", "jopandy");
		 * props.put("ssl.keystore.location",
		 * "/media/jothipandiyan/Bizruntime/tmp/kafka/password");
		 * props.put("ssl.keystore.type", "JKS"); props.put("compression.type",
		 * "gzip");
		 */
		props.put("bootstrap.servers", "localhost:9094");
		props.put("key.serializer",	"org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
		props.put("request.required.acks", "1");

		producer = new KafkaProducer<String, String>(props);

	}

	public static void main(String[] args) {

		SimpleProducer sim = new SimpleProducer();
		String topic = "NewProducer5";

		for (int i = 0; i < 10; i++) {

			String msg = " This is message " + i;

			ProducerRecord<String, String> data = new ProducerRecord<String, String>(
					topic, Integer.toString(i), msg);
			java.util.concurrent.Future<RecordMetadata> rs = producer.send(data, new Callback() {

						public void onCompletion(RecordMetadata recordMetadata,
								Exception e) {

							System.out.println("Received ack for partition="
									+ recordMetadata.partition() + " offset = "
									+ recordMetadata.offset());
						}
					});

			try {
				RecordMetadata rm = rs.get();
				msg = msg + "  partition = " + rm.partition() + " offset ="
						+ rm.offset();
				System.out.println(msg);
			} catch (Exception e) {
				System.out.println(e);
			}

		}
	}
}
