package com.spark.sql.examples;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.hive.HiveContext;

public class PartitionDiscoveryApp {

	private static Logger LOG = Logger.getLogger("LoadAndSaveFileApp");

	public void load() {
		SparkConf conf = new SparkConf().setAppName("LoadAndSaveFileApp")
				.setMaster("local[2]");

		JavaSparkContext jsc = new JavaSparkContext(conf);
		HiveContext sqlCtx = new HiveContext(jsc);

//		DataFrame df = sqlCtx.read().format("json").load("src/main/resources/PeopleByCountries.json");		
//		df.write().format("parquet").save("peopleByCountry");;

		DataFrame df = sqlCtx.sql("select * from parquet.`peopleByCountry`");
		df.write().saveAsTable("country");
		}

	public static void main(String[] args) {
		PartitionDiscoveryApp app = new PartitionDiscoveryApp();
		app.load();
	}
}
