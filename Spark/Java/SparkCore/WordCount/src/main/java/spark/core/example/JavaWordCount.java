package spark.core.example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;

import scala.Tuple2;

public final class JavaWordCount implements Serializable{
	
	private final Logger log = Logger.getLogger("JavaWordCount");

	public void countWordsLengthFromFiles() {
		
		SparkConf conf =  new SparkConf().setAppName("wordCount");
		
		JavaSparkContext ctx = new JavaSparkContext(conf);

		JavaRDD<String> lines = ctx.textFile("hdfs://192.168.1.193:9000/jo/rawData");
		// file must be in all nodes with same samth
//		JavaRDD<String> lines = ctx.textFile("file:///home/hduser/README.patentcite");
		
		JavaRDD<String> words = lines.flatMap(s -> Arrays.asList(s.split(" "))).persist(StorageLevel.MEMORY_ONLY());
		
		JavaPairRDD<String, Integer> ones = words.mapToPair(s -> new Tuple2<String, Integer>(s,1));
		
		JavaPairRDD<String, Integer>  counts = ones.reduceByKey((i1 , i2) -> i1+i2);
		
		List<Tuple2<String, Integer>> output = counts.collect();
		
		for(Tuple2<String, Integer> tuple : output)
			log.info("tuple -> "+tuple);		
		ctx.stop();
}

	public static void main(String[] args) throws InterruptedException {
		JavaWordCount num = new JavaWordCount();
		num.countWordsLengthFromFiles();
	}
}