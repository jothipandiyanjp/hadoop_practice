package com.spark.sql.examples;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

public class LoadAndSaveFileApp {

	private static Logger LOG = Logger.getLogger("LoadAndSaveFileApp");

	public void load() {
		SparkConf conf = new SparkConf().setAppName("LoadAndSaveFileApp")
				.setMaster("local[2]");

		JavaSparkContext jsc = new JavaSparkContext(conf);
		SQLContext sqlCtx = new SQLContext(jsc);

		 DataFrame df =sqlCtx.read().load("people.parquet/part-r-00000-69af47b7-df8f-4e10-ae4f-2c04a7c2e874.gz.parquet");
		
		 df.select("name").write()
				.save("src/main/resources/names.parquet");

		 df.select("name").write().format("json")
			.save("src/main/resources/names.json");
	}

	public static void main(String[] args) {
		LoadAndSaveFileApp app = new LoadAndSaveFileApp();
		app.load();
	}
}
