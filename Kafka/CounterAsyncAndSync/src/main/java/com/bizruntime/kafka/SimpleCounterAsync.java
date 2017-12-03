package com.bizruntime.kafka;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;


public class SimpleCounterAsync {
	private static Producer<String, String> producer;
	
    public static void main(String[] args) {
    asyncProducer();
    }
    
    public static void asyncProducer(){
    
    	ProducerConfig config=configure();
    
    	String topic = "Topic_1";
        String age = "new";
        int delay = 500;
        int count = 10;
        producer = new Producer<String, String>(config);	
        
        long startTime = System.currentTimeMillis();
        String mes="Starting..";
        produceMessages(topic, null, mes);
        
        for (int i=0; i < count; i++ ) {
        	produceMessages(topic,null,Integer.toString(i));
            try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }

        long endTime = System.currentTimeMillis();
        System.out.println("... and we are done. This took " + (endTime - startTime) + " ms.");
        produceMessages(topic,null,"... and we are done. This took " + (endTime - startTime) + " ms.");

        /* close shop and leave */
        producer.close();
        System.exit(0);
    }
    
    static void produceMessages(String topic, String key, String value){
    		 KeyedMessage<String, String> message = new KeyedMessage<String, String>(topic,key,value);
    		 producer.send(message);
    	}
    
    
     static ProducerConfig  configure(){
    	String brokerList ="localhost:9092";
        String sync = "async";
        Properties kafkaProps = new Properties();
        kafkaProps.put("metadata.broker.list", brokerList);
        kafkaProps.put("serializer.class", "kafka.serializer.StringEncoder");
        kafkaProps.put("request.required.acks", "1");
        kafkaProps.put("producer.type", sync);
        kafkaProps.put("send.buffer.bytes","550000");
        kafkaProps.put("receive.buffer.bytes","550000");
        ProducerConfig config=new ProducerConfig(kafkaProps);
        return config;
    }
	
}
