package com.spark.sql.examples;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.hive.HiveContext;

import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.immutable.Seq;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.runtime.AbstractFunction1;

public class SchemaMergeApp {

	private static Logger LOG = Logger.getLogger("LoadAndSaveFileApp");

	public void load() {
		SparkConf conf = new SparkConf().setAppName("LoadAndSaveFileApp")
				.setMaster("local[2]");

		JavaSparkContext jsc = new JavaSparkContext(conf);
		SQLContext sqlCtx = new SQLContext(jsc);
		JavaRDD<String>  rdd = jsc.textFile("src/main/resources/numbers.txt");
		
		JavaRDD<Numbers> rdd1 = rdd.<Numbers>map(f -> new Numbers(Integer.parseInt(f)*2));
		JavaRDD<Numbers> rdd2 = rdd.<Numbers>map(f -> new Numbers(Integer.parseInt(f)*3));
		
		DataFrame df1 = sqlCtx.createDataFrame(rdd1, Numbers.class);	
		DataFrame df2 = sqlCtx.createDataFrame(rdd1, Numbers.class);	

		DataFrame doubleDF = df1.toDF("double");
		DataFrame tripleDF = df2.toDF("triple");
		
		doubleDF.write().parquet("data/test_table/key=1");
		tripleDF.write().parquet("data/test_table/key=2");
		
		DataFrame df3 =  sqlCtx.read().option("mergeSchema", "true")
								.parquet("data/test_table");
		
		df3.printSchema();
		df3.show();
	}

	public static void main(String[] args) {
		SchemaMergeApp app = new SchemaMergeApp();
		app.load();
	}
}
class MapToInt extends AbstractFunction1<Row, String> implements Serializable{
	@Override
	public String apply(Row row) {
		System.out.println(row.getString(1));
		return row.getString(1);
	}
};
