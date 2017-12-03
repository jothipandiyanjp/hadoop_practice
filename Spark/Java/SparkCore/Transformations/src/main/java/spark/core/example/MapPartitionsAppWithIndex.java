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

public class MapPartitionsAppWithIndex implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger("MapPartitionsAppWithIndex");

	public void countLineLength() {
		
		SparkConf conf = new  SparkConf().setAppName("mapParitionWithINdex").setMaster("local[4]")
						.set("spark.executor.memory", "5g"); 

/*		SparkConf conf = new  SparkConf().setAppName("mapWithParition").setMaster("spark://192.168.1.7:7077")
						.set("spark.executor.memory", "2560m"); 
	*/	
//		conf.setJars(new String[]{"/media/jothipandiyan/Bizruntime/JothiPandiyan/Maven/NewWorkspace/Spark/Java/SparkCore/Transformations/target/Transformations-0.0.1-SNAPSHOT.jar"});
	
		JavaSparkContext ctx = new JavaSparkContext(conf);
		
		JavaRDD<String> lines= ctx.textFile("hdfs://192.168.1.193:9000/jo/README.patentcite");
		
//		JavaRDD<String> lines= ctx.textFile("hdfs://192.168.1.193:9000/jo/rawData");
		
		log.info("Lines Partition -> "+lines.partitions());

		JavaPairRDD<String, Integer>  pair = lines.mapToPair(s  ->  new Tuple2<String, Integer>(s,1));

		log.info("pair Partition -> "+pair.partitions());

/* 		
 * 		Similar to map and foreach
 * 		Difference is mapPartitions, objects will be initialize once per partition basis 
 * 		In case of map, object will be initialized element basis. 
 * 	JavaRDD<String> pairUpperCase =
*/
		JavaRDD<String> pairUpperCase = pair.mapPartitionsWithIndex( 
				(index,linesPerPartition) -> 
				{
					log.info("index -> "+index);

					List<String> upperCaselines = new ArrayList<String>(); // object will be created once

					while(linesPerPartition.hasNext()){
						String word = linesPerPartition.next()._1;
						upperCaselines.add(word.toUpperCase());
					}
					return upperCaselines.iterator();
				}
				,true
				);
		
		 log.info("PairToUpperCase Partition -> "+pairUpperCase.partitions());

		 pairUpperCase.collect();
		 //	 System.out.println(list);
	//	log.info("result -> "+ list);
		
	}
		
	public static void main(String[] args) throws InterruptedException {
		MapPartitionsAppWithIndex num = new MapPartitionsAppWithIndex();
		num.countLineLength();
	}

}

