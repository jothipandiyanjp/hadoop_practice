package spark.core.example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;

import scala.Tuple2;

public class FilterApp implements Serializable{
	
	private final Logger log = Logger.getLogger("FilterApp");

	public void countLineLength() {
		
		SparkConf conf = new  SparkConf().setAppName("map").setMaster("local");
		
		JavaSparkContext ctx = new JavaSparkContext(conf);
		
		JavaRDD<String> lines= ctx.textFile("src/main/resources/README");
		
		JavaRDD<String> words = lines.flatMap(s -> Arrays.asList(s.split(" ")));
		
		JavaRDD<String> onlywords = words.filter(s ->  s.contains("author"));
		
		JavaPairRDD<String, Integer>  pair = onlywords.mapToPair(s  ->  new Tuple2<String, Integer>(s.trim(),1));

		JavaPairRDD<String, Integer> reducedPair = pair.reduceByKey((i1, i2) -> i1+i2 );
		
		reducedPair.collect().stream().forEach(s -> log.info(s));;
		
	}
		
	public static void main(String[] args) throws InterruptedException {
		FilterApp num = new FilterApp();
		num.countLineLength();
	}

}

