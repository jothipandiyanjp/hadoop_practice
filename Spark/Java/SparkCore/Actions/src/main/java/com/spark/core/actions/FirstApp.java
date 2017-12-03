package com.spark.core.actions;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;

import scala.Tuple2;

public class FirstApp {
	private final Logger log = Logger.getLogger("SaveAsTextFile");

	public void countWordsLengthFromFiles() {
		
//		SparkConf conf =  new SparkConf().setAppName("wordCount").setMaster("spark://192.168.1.7:7077");
	
		SparkConf conf =  new SparkConf().setAppName("wordCount").setMaster("local");
		
		JavaSparkContext ctx = new JavaSparkContext(conf);

//		JavaRDD<String> lines = ctx.textFile("hdfs://192.168.1.193:9000/jo/rawData");
		// file must be in all nodes with same samth
	
		JavaRDD<String> lines = ctx.textFile("src/main/resources/README");
		
		JavaRDD<String> words = lines.flatMap(s -> Arrays.asList(s.split(" "))).persist(StorageLevel.MEMORY_ONLY());
		
		JavaPairRDD<Integer, String> ones = words.mapToPair(s -> new Tuple2<Integer, String>(1,s));
		
		
		log.info("result ->"+ones.first());
		ctx.stop();
}

	public static void main(String[] args) throws InterruptedException {
		FirstApp num = new FirstApp();
		num.countWordsLengthFromFiles();
	}
}