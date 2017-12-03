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


public class ReduceByApp implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger("GroupByApp");

	public void countLineLength() {
		
		SparkConf conf = new  SparkConf().setAppName("mapParitionWithINdex").setMaster("local")
						.set("spark.executor.memory", "4g"); 

		JavaSparkContext ctx = new JavaSparkContext(conf);
		
		JavaRDD<String> lines= ctx.textFile("hdfs://192.168.1.193:9000/jo/Sample.csv");

		lines.toDebugString();
		
		JavaPairRDD<String, Integer>  pair = lines.mapToPair(f -> new Tuple2<String, Integer>(f, 1));
		
		JavaPairRDD<String, Integer> reducer = pair.reduceByKey((i1,i2)->i1+i2);
		
		log.info("count -> "+reducer.count());
		log.info("result -> " +reducer.collect());
		
	}
		
	public static void main(String[] args) throws InterruptedException {
		ReduceByApp num = new ReduceByApp();
		num.countLineLength();
	}

}

