package spark.core.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.Partition;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.rdd.HadoopPartition;

import scala.Tuple2;

public class MapPartitionsApp {
	
	private final Logger log = Logger.getLogger("MapPartitionsApp");

	public void countLineLength() {
		
		SparkConf conf = new  SparkConf().setAppName("map").setMaster("local");
	
//		SparkConf conf = new  SparkConf().setAppName("mapWithParition").setMaster("spark://192.168.1.7:7077");
		JavaSparkContext ctx = new JavaSparkContext(conf);
		
		JavaRDD<String> lines= ctx.textFile("hdfs://192.168.1.193:9000/jo/README.patentcite");
		

		log.info("Lines Partition -> "+lines.partitions());

		JavaPairRDD<String, Integer>  pair = lines.mapToPair(s  ->  new Tuple2<String, Integer>(s,1));

		log.info("pair Partition -> "+pair.partitions());

/* 		
 * 		Similar to map and foreach
 * 		Difference is mapPartitions, objects will be initialize once per partition basis 
 * 		In case of map, object will be initialized element basis. 
*/
		JavaRDD<String> pairUpperCase = pair.mapPartitions(linesPerPartition -> 
				{
					
					List<String> upperCaselines = new ArrayList<String>(); // object will be created once

					while(linesPerPartition.hasNext()){
						upperCaselines.add(linesPerPartition.next()._1.toUpperCase());
					}
					return upperCaselines;
				}
				);
		 log.info("PairToUpperCase Partition -> "+pairUpperCase.partitions());

		 List<String> list = pairUpperCase.collect();
	//	log.info("result -> "+ list);
		
	}
		
	public static void main(String[] args) throws InterruptedException {
		MapPartitionsApp num = new MapPartitionsApp();
		num.countLineLength();
	}

}

