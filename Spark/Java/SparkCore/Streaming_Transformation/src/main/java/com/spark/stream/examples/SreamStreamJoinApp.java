package com.spark.stream.examples;

import java.lang.reflect.Array;
import java.time.Duration;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.StorageLevels;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

import com.google.common.base.Optional;

public class SreamStreamJoinApp {
	private final Logger LOGGER =Logger.getLogger("SreamStreamJoinApp");
	
	
	public void example(){
		
		SparkConf conf = new SparkConf().setAppName("StreamingAppName").setMaster("local[2]");
		
		JavaStreamingContext ctx = new JavaStreamingContext(conf,Durations.seconds(2));
	    Logger.getLogger("org").setLevel(Level.OFF);
	    
		JavaReceiverInputDStream<String> lines =  ctx.socketTextStream("localhost", 9999, StorageLevels.MEMORY_AND_DISK);
		
		JavaDStream<String>  words = lines.flatMap((String s) -> Arrays.asList(s.split(" "))  );
		
		JavaPairDStream<String, Integer>  pair1 = words.mapToPair( (String s) -> 	new Tuple2<String, Integer>(s, 1));
		
		JavaPairDStream<String, Integer>  pair2 = words.mapToPair( (String s) -> 	new Tuple2<String, Integer>(s, 1));

		
		 JavaPairDStream<String, Tuple2<Integer, Integer>>  join = pair1.join(pair2);
		
		// wait for 30 seconds and notice the output
		// every 10 seconds sliding window
		join.print();
		// if hello word -> (hello ,1)  (world,1)
		
		ctx.start();
		ctx.awaitTermination();
	}
	
	public static void main(String[] args) {
		SreamStreamJoinApp app = new SreamStreamJoinApp();
		app.example();
	}
}
