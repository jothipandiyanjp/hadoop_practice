package com.spark.sql.examples;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.hive.HiveContext;


public class HiveContextApp implements Serializable {

	private static Logger LOG = Logger.getLogger("HiveContextApp");

	public void load() {
		SparkConf conf = new SparkConf().setAppName("HiveContextApp")
				.setMaster("spark://192.168.1.8:7077");

		JavaSparkContext jsc = new JavaSparkContext(conf);
		HiveContext hiveCtx = new HiveContext(jsc);
		
		hiveCtx.setConf("url","jdbc:hive2://0.0.0.0:10000/default");
		hiveCtx.setConf("user","root");
		hiveCtx.setConf("password","root");
		hiveCtx.setConf("driver","org.apache.hive.jdbc.HiveDriver");
		
		hiveCtx.setConf("hive.metastore.uris","thrift://localhost:9083");
		hiveCtx.setConf("hive.server2.thrift.bind.host","localhost");
		hiveCtx.setConf("hive.server2.thrift.port","10000");
	
		DataFrame df = hiveCtx.sql("show tables");

		df.show();
		

	}

	public static void main(String[] args) {
		HiveContextApp app = new HiveContextApp();
		app.load();
	}
}

