package com.spark.stream.examples;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

public class InferSchemaApp implements Serializable{
	public void example() {
		SparkConf conf = new SparkConf().setAppName("IferSchemaApp").setMaster(
				"local[*]");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		SQLContext sqlCtx = new SQLContext(jsc);

		JavaRDD<Person> people = jsc.textFile("src/main/resources/people.txt")
				.map((String line) -> {
					String[] parts = line.split(",");
					Person person = new Person();
					person.setName(parts[0]);
					person.setAge(Integer.parseInt(parts[1].trim()));

					return person;
				});


		DataFrame schemaPeople = sqlCtx.createDataFrame(people, Person.class);
	
		schemaPeople.registerTempTable("people");
		
		DataFrame teenagers = sqlCtx.sql("SELECT name,age FROM people WHERE age >= 13 AND age <= 19");
		
		//teenagers.show();

		List<String> teenagerNames = teenagers.javaRDD().map((Row row) -> 
		 "Name : "+row.getString(0)+" age: "+row.getInt(1)
		).collect();
	
	System.out.println(teenagerNames);	
	

	}

	public static void main(String[] args) {
		InferSchemaApp app = new InferSchemaApp();
		app.example();
	}
}
