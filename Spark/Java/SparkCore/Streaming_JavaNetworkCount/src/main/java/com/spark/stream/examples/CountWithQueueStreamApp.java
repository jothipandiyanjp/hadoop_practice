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
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;
import scala.concurrent.duration.Duration;

public class CountWithQueueStreamApp implements Serializable {
	private final Logger LOGGER = Logger.getLogger("JavaNetworkCount");

	@SuppressWarnings("deprecation")
	public void wordCount() {
		SparkConf conf = new SparkConf().setAppName("StreamingAppName")
				.setMaster("local[*]");

		JavaStreamingContext ctx = new JavaStreamingContext(conf,
				Durations.seconds(2));

		JavaReceiverInputDStream<String> lines = ctx.socketTextStream(
				"localhost", 9999, StorageLevels.MEMORY_AND_DISK);

		JavaDStream<String> words = lines.flatMap((String s) -> Arrays.asList(s
				.split(" ")));

		Queue<JavaRDD<String>> queue = new LinkedList<JavaRDD<String>>();
		
		words.foreachRDD(new VoidFunction<JavaRDD<String>>() {
			@Override
			public void call(JavaRDD<String> rdd) throws Exception {
				System.out.println("->");
				queue.add(rdd);
			}
		});

		JavaDStream<String> w = ctx.queueStream(queue);
		
		JavaPairDStream<String, Integer> pairs = w
				.mapToPair((String s) ->{System.out.println("->");return new Tuple2<String, Integer>(s, 1);});

		JavaPairDStream<String, Integer> wordCount = pairs.reduceByKey((
				Integer i1, Integer i2) -> i1 + i2);

		w.count().print();
		ctx.start();
		ctx.awaitTermination();

	}

	public static void main(String[] args) {
		CountWithQueueStreamApp app = new CountWithQueueStreamApp();
		app.wordCount();
	}
}
