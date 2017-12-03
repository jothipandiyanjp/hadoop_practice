package com.spark.stream.examples;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

public class QueueStreamApp implements Serializable {
	private final Logger LOGGER = Logger.getLogger("QueueStreamApp");

	public void wordCount() throws ClassNotFoundException {
		
		SparkConf conf = new SparkConf().setAppName("fileStream").setMaster("local[3]")
								.registerKryoClasses(new Class<?>[]{
									    Class.forName("org.apache.hadoop.io.LongWritable"),
									    Class.forName("org.apache.hadoop.io.Text")
								});

		JavaStreamingContext jssc = new JavaStreamingContext(conf,Durations.seconds(2));
		
		Queue<JavaRDD<String>> queue = new LinkedList<JavaRDD<String>>();
		for(int i =0; i<10;i++){
			queue.add(jssc.sparkContext().parallelize(Arrays.asList(i+"")));
		}
		JavaDStream<String> dS = jssc.queueStream(queue);
		
				queue.add(jssc.sparkContext().parallelize(Arrays.asList("10.")));
		dS.print();
		
//		dStream.count().print();
		
//		usefulInput.count().print();;
		
		jssc.start();
		jssc.awaitTermination();
	}

	public static void main(String[] args) throws ClassNotFoundException {
		QueueStreamApp app = new QueueStreamApp();
		app.wordCount();
	}
}
