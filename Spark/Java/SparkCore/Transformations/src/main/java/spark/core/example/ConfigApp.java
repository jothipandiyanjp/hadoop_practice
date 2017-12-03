package spark.core.example;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class ConfigApp {
	private final Logger log = Logger.getLogger("ConfigApp");

	public void getAllConfig() {
		SparkConf conf = new  SparkConf().setAppName("map").setMaster("local");
		JavaSparkContext ctx = new JavaSparkContext(conf);
		
		for(Tuple2<String, String> tuple2 : ctx.getConf().getAll()){
			log.info("Tuple -> "+tuple2);
		}

	}
		
	public static void main(String[] args) throws InterruptedException {
		ConfigApp num = new ConfigApp();
		num.getAllConfig();
	}

}

