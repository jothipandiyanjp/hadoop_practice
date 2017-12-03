package com.spark.stream.examples;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.Accumulator;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function0;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

public class JavaRecoverableNetworkWordCountApp implements Serializable {

	public void example() {
	
		
		Function0<JavaStreamingContext> createContextFunc = new Function0<JavaStreamingContext>() {
			SparkConf sparkConf = new SparkConf().setAppName("JavaRecoverableNetworkWordCount")
					.setMaster("local[*]");

			@Override
			public JavaStreamingContext call() throws Exception {
				JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, Durations.seconds(1));
				return ssc;
			}
		};

		JavaStreamingContext ssc = JavaStreamingContext.getOrCreate("target5/",
				createContextFunc);

		

		JavaReceiverInputDStream<String> lines = ssc.socketTextStream(
				"localhost", 9998);

		JavaDStream<String> words = lines.flatMap((String s) -> Arrays.asList(s
				.split(" ")));

		JavaPairDStream<String, Integer> wordCounts = words.mapToPair(
				(String s) -> new Tuple2<String, Integer>(s, 1)).reduceByKey(
				(Integer i1, Integer i2) -> i1 + i2);

		wordCounts
				.foreachRDD((JavaPairRDD<String, Integer> rdd, Time time) -> {
					final Broadcast<List<String>> blacklist = JavaWordBlacklist
							.getInstance(new JavaSparkContext(rdd.context()));

					final Accumulator<Integer> droppedWordsCounter = JavaDroppedWordsCounter
							.getInstance(new JavaSparkContext(rdd.context()));
					
					String counts = rdd
							.filter((Tuple2<String, Integer> wordCount) -> {
								if (blacklist.value().contains(wordCount._1())) {  // comparing BRODCAST value and received value

									droppedWordsCounter.add(wordCount._2());
									return false;
								} else {
									return true;
								}
							}).collect().toString();
					
					// pass a or b or  c  from netcat and check ouput
					String output = "Counts at time " + time + " " + counts;
					System.out.println(output);
					System.out.println("Dropped " + droppedWordsCounter.value()
							+ " word(s) totally");

				});
		
		ssc.start();
		ssc.awaitTermination();
		ssc.checkpoint("target5/");

	}

	public static void main(String[] args) {
		JavaRecoverableNetworkWordCountApp app = new JavaRecoverableNetworkWordCountApp();
		app.example();

	}
}
