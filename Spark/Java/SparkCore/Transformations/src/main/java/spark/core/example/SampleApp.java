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

public class SampleApp implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger("SampleApp");

	public void countLineLength() {
		
		SparkConf conf = new  SparkConf().setAppName("mapParitionWithINdex").setMaster("local")
						.set("spark.executor.memory", "4g"); 

/*		SparkConf conf = new  SparkConf().setAppName("mapWithParition").setMaster("spark://192.168.1.7:7077")
						.set("spark.executor.memory", "2560m"); 
*/		
//		conf.setJars(new String[]{"/media/jothipandiyan/Bizruntime/JothiPandiyan/Maven/NewWorkspace/Spark/Java/SparkCore/Transformations/target/Transformations-0.0.1-SNAPSHOT.jar"});
	
		JavaSparkContext ctx = new JavaSparkContext(conf);
		
		JavaRDD<String> lines= ctx.textFile("hdfs://192.168.1.193:9000/jo/README.patentcite");
		
/*		JavaRDD<String> lines= ctx.textFile("hdfs://192.168.1.193:9000/jo/rawData");
*/		
		log.info("Lines Partition -> "+lines.partitions());

		JavaPairRDD<String, Integer>  pair = lines.mapToPair(s  ->  new Tuple2<String, Integer>(s,1));

		log.info("pair Partition -> "+pair.partitions());

		JavaRDD<Integer> pairUpperCase = pair.mapPartitionsWithIndex( 
				(index,linesPerPartition) -> 
				{
					log.info("index -> "+index);
					System.out.println("index -> "+index);
					Integer num = 0;
					while(linesPerPartition.hasNext()){
						linesPerPartition.next();
						num++;
					}
					List<Integer> total = new ArrayList<Integer>();
					total.add(num);
					return total.iterator();
				}
				,true
				);
		
		 log.info("PairToUpperCase Partition -> "+pairUpperCase.partitions());
		
		 // It will return given fraction result from given rdd
		 log.info("sample -->"+pairUpperCase.sample(true, .5).collect()); // sample return rdd

		log.info("result -->"+pairUpperCase.collect());
	 
	//	 System.out.println(list);
	//	log.info("result -> "+ list);
		
	}
		
	public static void main(String[] args) throws InterruptedException {
		SampleApp num = new SampleApp();
		num.countLineLength();
	}

}

