package com.spark.stream.examples;

import java.lang.reflect.Array;
import java.util.Arrays;

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

public class TransformToPairApp {
	private final Logger LOGGER =Logger.getLogger("TransformApp");
	
	
	public void example(){
		
		SparkConf conf = new SparkConf().setAppName("StreamingAppName").setMaster("local[2]");
		
		JavaStreamingContext ctx = new JavaStreamingContext(conf,Durations.seconds(2));
		
		ctx.checkpoint("checkpoint");
		
		

		JavaReceiverInputDStream<String> lines =  ctx.socketTextStream("localhost", 9999, StorageLevels.MEMORY_AND_DISK);
		
		JavaDStream<String>  words = lines.flatMap((String s) -> Arrays.asList(s.split(" "))  );
		
		JavaPairDStream<String, Integer>  pairs = words.mapToPair( (String s) -> 	new Tuple2<String, Integer>(s, 1));
		
		JavaPairDStream<String, Integer> wordCounts = pairs.updateStateByKey( (nums, current) -> {
			Integer sum = current.or(0);
			for (Integer i : nums) {
				sum += i;
			}
			return Optional.of(sum);
	   	}); 
		
		JavaRDD<String> rdd1= ctx.sparkContext().parallelize(Arrays.asList("hello","hi"));
		JavaPairRDD<String, Integer>  rddPair = rdd1.mapToPair(f -> new Tuple2<String, Integer>(f,1));

		wordCounts.transformToPair(f -> f.join(rddPair)).print(); // matching word will be joined
		
		// output will be if hello -> hello ,(1,1) 
		//                if  hi  ->  hi,  (1,1)
		//				  if hai   ->  
		
		ctx.start();
		ctx.awaitTermination();
	}
	
	public static void main(String[] args) {
		TransformToPairApp app = new TransformToPairApp();
		app.example();
	}
}
