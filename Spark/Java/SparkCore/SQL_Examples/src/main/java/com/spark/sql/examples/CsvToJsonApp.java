package com.spark.sql.examples;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

public class CsvToJsonApp {
	public void example(){
			SparkConf conf = new SparkConf().setAppName("csv2json").setMaster("local[4]");
			JavaSparkContext jsc = new JavaSparkContext(conf);
			SQLContext sqlCtx = new SQLContext(jsc);

//			sqlCtx.read().format("com.databricks.spark.csv").load("hdfs://192.168.1.193:9000/jo/Sample.csv").show();

			DataFrame df = sqlCtx.read().format("com.databricks.spark.csv").option("header", "false")
			.option("inferSchema", "true"). 
				  load("src/main/resources/Sample.csv");

			// writing to a json file
			df.write().format("json").save("src/main/resources/csv2json");
			
	}	
	
	public static void main(String[] args) {
		CsvToJsonApp app =new  CsvToJsonApp();
		app.example();
	}
}
