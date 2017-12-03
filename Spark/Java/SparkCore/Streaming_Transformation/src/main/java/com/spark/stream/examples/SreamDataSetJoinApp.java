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

public class SreamDataSetJoinApp {
	private final Logger LOGGER =Logger.getLogger("SreamStreamJoinApp");
	
	
	public void example(){
		
		SparkConf conf = new SparkConf().setAppName("StreamingAppName").setMaster("local[2]");
		
		JavaStreamingContext ctx = new JavaStreamingContext(conf,Durations.seconds(2));
	    Logger.getLogger("org").setLevel(Level.OFF);
	    
		JavaReceiverInputDStream<String> lines =  ctx.socketTextStream("localhost", 9999, StorageLevels.MEMORY_AND_DISK);
		
		JavaDStream<String>  words = lines.flatMap((String s) -> Arrays.asList(s.split(" "))  );
		
		JavaPairDStream<String, String>  stream = words.mapToPair( (String s) -> 	new Tuple2<String, String>(s, s));
		
		JavaPairDStream<String, String> windowedStream = stream.window(Durations.seconds(20));

		JavaPairRDD<String, String> dataset = ctx.sparkContext().parallelize(Arrays.asList("hello","hi"))
				.mapToPair( (String s) -> 	new Tuple2<String, String>(s, s));

		
		
		 windowedStream.transform(f -> f.map(m -> m._2));
		
		
		ctx.start();
		ctx.awaitTermination();
	}
	
	public static void main(String[] args) {
		SreamDataSetJoinApp app = new SreamDataSetJoinApp();
		app.example();
	}
}
