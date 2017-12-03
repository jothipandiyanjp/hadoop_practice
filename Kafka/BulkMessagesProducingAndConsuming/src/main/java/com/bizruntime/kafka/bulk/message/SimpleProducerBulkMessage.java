package com.bizruntime.kafka.bulk.message;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class SimpleProducerBulkMessage extends  Thread {
	
	private static Logger LOGGER=Logger.getLogger(SimpleProducerBulkMessage.class);
	private static Producer<String, String> producer;
	private final Properties props = new Properties();

		
	public SimpleProducerBulkMessage() {
		InputStream in = SimpleProducerBulkMessage.class.getClassLoader()
				.getResourceAsStream("kafkaProducer.properties");
		try {
			props.load(in);
		} catch (IOException e) {
			LOGGER.error("While loading property file exception occured  in Producer Class: "+e.getMessage());
		}
		
		producer = new Producer<String, String>(new ProducerConfig(props));
 
	}

	public static void main(String[] args) {
		
		SimpleProducerBulkMessage sp = new SimpleProducerBulkMessage();
		
		List<KeyedMessage<String, String>> list=new ArrayList<KeyedMessage<String,String>>();
		for(int i=0;i<10;i++){
			KeyedMessage<String, String> messages=getMessage();
			list.add(messages);
			LOGGER.debug("Messages to be  add in list: "+messages.message());
		}
		producer.send(list);
		producer.close();
	}
	
	public static KeyedMessage<String, String>  getMessage(){
		BufferedReader br = null;
		String message=null;
		try {
			br = new BufferedReader(new FileReader("src/main/resources/readme.txt"));
		  StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    message = sb.toString();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
		    try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		KeyedMessage<String, String> data = new KeyedMessage<String, String>("BulkMessage",message);		
		return data;
	}
}
