package com.spark.sql.examples;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

public class CreateParquetApp {

	private static Logger LOG = Logger.getLogger("LoadAndSaveFileApp");

	public void load() {
		SparkConf conf = new SparkConf().setAppName("LoadAndSaveFileApp")
				.setMaster("local[2]");

		JavaSparkContext jsc = new JavaSparkContext(conf);
		SQLContext sqlCtx = new SQLContext(jsc);


		DataFrame df= sqlCtx.read().format("json").load("src/main/resources/people.json");
		df.write().format("parquet").save("people.parquet");
		
	}

	public static void main(String[] args) {
		CreateParquetApp app = new CreateParquetApp();
		app.load();
	}
}
