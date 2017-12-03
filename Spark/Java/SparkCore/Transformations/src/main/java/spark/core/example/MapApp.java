package spark.core.example;

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;

import scala.Tuple2;

public class MapApp implements Serializable{
	
	private final Logger log = Logger.getLogger("MapApp");

	public void countLineLength() {
		
		SparkConf conf = new  SparkConf().setAppName("map").setMaster("local");
		
		JavaSparkContext ctx = new JavaSparkContext(conf);
		
		JavaRDD<String> lines= ctx.textFile("src/main/resources/README");
		
		JavaPairRDD<String, Integer>  length = lines.mapToPair(s  ->  new Tuple2<String, Integer>(s,1));
		 
		Tuple2<String, Integer> output = length.fold(new Tuple2<String, Integer>("", new Integer(0)),
				 			(v1,v2) -> new Tuple2<String, Integer>("",v1._2+v2._2()));
		
		log.debug(output._2);
		
	}
		
	public static void main(String[] args) throws InterruptedException {
		MapApp num = new MapApp();
		num.countLineLength();
	}

}

