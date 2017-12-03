package com.spark.stream.examples;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class ProgrammaticSpecifySchemaApp {
	public void example(){			
		SparkConf conf = new SparkConf().setAppName("ProgrammaticSpecifySchemaApp").setMaster("local[2]");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		SQLContext sqlContext = new SQLContext(jsc);
		
		JavaRDD<String> people = jsc.textFile("src/main/resources/people.txt");
		
		String schemaString = "name age";
		
		List<StructField> fields = new ArrayList<StructField>();
		for(String field : schemaString.split(" "))
			fields.add(DataTypes.createStructField(field, DataTypes.StringType, true)); // true mentions nullable true
		
		StructType schema  = DataTypes.createStructType(fields);
		
		JavaRDD<org.apache.spark.sql.Row> rowRdd = people.map((String record) -> {
					String[] fieldss = record.split(",");
					return RowFactory.create(fieldss[0],fieldss[1].trim());
				} );
		
		DataFrame peopleDataFrame = sqlContext.createDataFrame(rowRdd, schema);
		
		peopleDataFrame.registerTempTable("people");
		
		DataFrame result= sqlContext.sql("select * from people");
		
		List<String> names = result.javaRDD().map((Row row) ->  "Name : "+row.getString(0)).collect();
		
		System.out.println(names);
	}
	public static void main(String[] args) {
		ProgrammaticSpecifySchemaApp app = new ProgrammaticSpecifySchemaApp();
		app.example();		
	}
}
