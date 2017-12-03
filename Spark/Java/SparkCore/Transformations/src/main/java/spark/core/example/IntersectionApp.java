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

public class IntersectionApp implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger("SampleApp");

	public void countLineLength() {
		
		SparkConf conf = new  SparkConf().setAppName("mapParitionWithINdex").setMaster("local")
						.set("spark.executor.memory", "4g"); 


		JavaSparkContext ctx = new JavaSparkContext(conf);
		
		JavaRDD<String> lines= ctx.textFile("src/main/resources/Numbers");
		JavaRDD<String> lines1= ctx.textFile("src/main/resources/Numbers1");
				
		JavaRDD<Integer> num = lines.map(s -> Integer.parseInt(s));
		JavaRDD<Integer> num1 = lines1.map(s -> Integer.parseInt(s));

		
		// union 
		log.info("union -> "+num.intersection(num1).collect());
		
		
		
	}
		
	public static void main(String[] args) throws InterruptedException {
		IntersectionApp num = new IntersectionApp();
		num.countLineLength();
	}

}

