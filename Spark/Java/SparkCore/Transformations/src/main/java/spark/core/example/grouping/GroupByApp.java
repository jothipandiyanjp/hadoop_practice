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


public class GroupByApp implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger("GroupByApp");

	public void countLineLength() {
		
		SparkConf conf = new  SparkConf().setAppName("mapParitionWithINdex").setMaster("local[2]")
						.set("spark.executor.memory", "4g"); 

		JavaSparkContext ctx = new JavaSparkContext(conf);
		
		JavaRDD<String> lines= ctx.textFile("hdfs://192.168.1.193:9000/jo/Sample.csv");

		lines.toDebugString();
		
		JavaRDD<Tuple2<Integer, CsvToJava>> listOfData = 	lines.mapPartitions(iterator -> {
			List<Tuple2<Integer, CsvToJava>> data = new ArrayList<Tuple2<Integer, CsvToJava>>();  
			while(iterator.hasNext()){
				String[] row = iterator.next().split(",");
				CsvToJava obj = new CsvToJava(row[1], row[2], row[3], Double.parseDouble(row[4]));
				data.add( new Tuple2<Integer, CsvToJava>(Integer.parseInt(row[0]), obj));
			}
			return 	data;	
		});
		
		JavaPairRDD<String, Iterable<Tuple2<Integer, CsvToJava>>> groupedData = 
				listOfData.groupBy(f -> f._2.getName());
		
		List<Tuple2<String, Iterable<Tuple2<Integer, CsvToJava>>>> result = groupedData.collect();
		for(int i=0;i<result.size();i++)
			log.info("result -> "+result.get(i));
	}
		
	public static void main(String[] args) throws InterruptedException {
		GroupByApp num = new GroupByApp();
		num.countLineLength();
	}

}

