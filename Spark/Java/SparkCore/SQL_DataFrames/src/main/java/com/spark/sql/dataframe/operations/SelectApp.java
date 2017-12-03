package com.spark.sql.dataframe.operations;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

public class SelectApp {

	public void example() {
		SparkConf conf  =  new SparkConf().setAppName("dataframeApp")
										.setMaster("local[*]");
		
		JavaSparkContext jsc = new JavaSparkContext(conf);
		SQLContext sqlCtx =  new SQLContext(jsc);
		DataFrame df = sqlCtx.read().json("src/main/resources/people.json");
		
		df.select("name").show(); // selects only column name
	}

	public static void main(String[] args) {
		SelectApp app = new SelectApp();
		app.example();
	}
}
