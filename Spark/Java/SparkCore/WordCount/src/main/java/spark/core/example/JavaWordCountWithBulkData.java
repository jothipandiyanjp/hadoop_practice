package spark.core.example;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;

import scala.Tuple2;

public final class JavaWordCountWithBulkData implements Serializable{
	private final Logger log = Logger.getLogger("JavaWordCount");

	public void countWordsLengthFromFiles() {
		// if it was yarn cluster. remove setMaster
		SparkConf conf =  new SparkConf().setAppName("bulkData").setMaster("spark://192.168.1.8:7077");
		
		JavaSparkContext ctx = new JavaSparkContext(conf);
		
//		JavaRDD<String> lines = ctx.textFile("hdfs://192.168.1.193:9000/jo/README.patentcite");
//		File must be in all nodes 
		JavaRDD<String> lines = ctx.textFile("hdfs://192.168.1.193:9000/jo/rawData");
		
// split the words and finally flatten the result		
		JavaRDD<String> words = lines.flatMap(s -> Arrays.asList(s.split(" ")));
		
		JavaPairRDD<String, Integer> ones = words.mapToPair(s -> new Tuple2<String, Integer>(s,1));
		
		JavaPairRDD<String, Integer>  counts = ones.reduceByKey((i1 , i2) -> i1+i2);
		
		List<Tuple2<String, Integer>> output = counts.collect();
		
		for(Tuple2<String, Integer> tuple : output)
			log.info("tuple -> "+tuple);		

}

	public static void main(String[] args) throws InterruptedException {
		JavaWordCountWithBulkData num = new JavaWordCountWithBulkData();
		num.countWordsLengthFromFiles();
	}
}