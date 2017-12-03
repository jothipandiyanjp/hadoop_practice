package com.bizruntime.kafka;

public class Demo {
public static void main(String[] args) {
	SimpleProducer producer=new SimpleProducer();
	producer.start();
	//SimpleHighLevelConsumer consumer =new SimpleHighLevelConsumer("BulkMessage");
	//consumer.start();
	
}
}
