package com.spark.stream.examples;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Optional;

import java.util.Queue;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.StorageLevels;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;
import scala.concurrent.duration.Duration;

public class CountApp implements Serializable{
	private final Logger LOGGER =Logger.getLogger("JavaNetworkCount");
	
	
	public void wordCount(){
		SparkConf conf = new SparkConf().setAppName("StreamingAppName").setMaster("local[2]");
		
		JavaStreamingContext ctx = new JavaStreamingContext(conf,Durations.seconds(2));
		
		JavaReceiverInputDStream<String> lines =  ctx.socketTextStream("localhost", 9999, StorageLevels.MEMORY_AND_DISK);
		
		JavaDStream<String>  words = lines.flatMap((String s) -> Arrays.asList(s.split(" "))  );
		
		
		JavaPairDStream<String, Integer>  pairs = words.mapToPair( (String s) -> 	new Tuple2<String, Integer>(s, 1));
				
		JavaPairDStream<String, Integer> wordCount = pairs.reduceByKey((Integer i1, Integer i2) -> i1 + i2 );
		
		wordCount.print();
		
		ctx.start();
		ctx.awaitTermination();
		
	}
	
	public static void main(String[] args) {
		CountApp app = new CountApp();
		app.wordCount();
	}
}
