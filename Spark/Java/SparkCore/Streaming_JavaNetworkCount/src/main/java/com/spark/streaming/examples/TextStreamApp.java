package com.spark.streaming.examples;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Optional;

import java.util.Queue;
import java.util.function.Consumer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

public class TextStreamApp implements Serializable {
	private final Logger LOGGER = Logger.getLogger("FileStreamApp");

	public void wordCount() throws ClassNotFoundException {
		
		SparkConf conf = new SparkConf().setAppName("fileStream").setMaster("local[3]")
								.registerKryoClasses(new Class<?>[]{
									    Class.forName("org.apache.hadoop.io.LongWritable"),
									    Class.forName("org.apache.hadoop.io.Text")
								});

		JavaStreamingContext jssc = new JavaStreamingContext(conf,Durations.seconds(2));
		
		JavaPairInputDStream<LongWritable,Text> dStream =	jssc.fileStream("hdfs://192.168.1.193:9000/jo/", 
							LongWritable.class, Text.class, TextInputFormat.class);	

		JavaDStream<Tuple2<LongWritable, Text>> d =dStream.map(s -> {System.out.println("--->"+s);return s;});
		
		d.print();
		
//		dStream.count().print();
		
//		usefulInput.count().print();;
		
		jssc.start();
		jssc.awaitTermination();
	}

	public static void main(String[] args) throws ClassNotFoundException {
		TextStreamApp app = new TextStreamApp();
		app.wordCount();
	}
}
