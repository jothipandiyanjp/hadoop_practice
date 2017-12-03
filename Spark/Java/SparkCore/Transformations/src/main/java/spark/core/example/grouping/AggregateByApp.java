package spark.core.example.grouping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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






import scala.Tuple2;import scala.collection.mutable.HashMap;


public class AggregateByApp implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger("AggregateByApp");

	public void countLineLength() {
		
		SparkConf conf = new  SparkConf().setAppName("mapParitionWithINdex").setMaster("local[2]")
						.set("spark.executor.memory", "4g"); 

		JavaSparkContext ctx = new JavaSparkContext(conf);
		
//		JavaRDD<String> lines= ctx.textFile("hdfs://192.168.1.193:9000/jo/Sample.csv");
		JavaRDD<String> lines= ctx.textFile("src/main/resources/Numbers1");

		lines.toDebugString();
		
		JavaPairRDD<String, Integer>  pair = lines.mapToPair(f -> new Tuple2<String, Integer>(f, 1));
		
		
		JavaPairRDD<String, Integer> reducer = pair.aggregateByKey(0,
					(v1,v2) -> v2+v1,			// 	  0 + v2
					(acc1, acc2) -> acc1+acc2   // count duplicate elements
					);
		
		log.info("result -> " +reducer.collect());
		
	}
		
	public static void main(String[] args) throws InterruptedException {
		AggregateByApp num = new AggregateByApp();
		num.countLineLength();
	}

}

