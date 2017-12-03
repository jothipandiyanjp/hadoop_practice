package com.spark.core.actions;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.rdd.JdbcRDD;

import scala.runtime.AbstractFunction0;
import scala.runtime.AbstractFunction1;
import static scala.reflect.ClassTag$.MODULE$;

public class JDBCRDDApp implements Serializable{
	
	
	class MapResultObjectArray extends AbstractFunction1<ResultSet, Object[]> implements Serializable {

	    public Object[] apply(ResultSet row) {    	
				return JdbcRDD.resultSetToObjectArray(row);
	    }
	}
	
	
	class MapResultString extends AbstractFunction1<ResultSet, String> implements Serializable {

	    public String apply(ResultSet row) {    	
	    	
	    	try {
				return row.getString(1);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	throw new NullPointerException();
	    }
	}
	
	class GetConnection extends AbstractFunction0<Connection> implements Serializable {
	    public Connection apply(){
	        try {
                Class.forName("com.mysql.jdbc.Driver");

				return DriverManager.getConnection("jdbc:mysql://localhost:3306/empdb", "root", "root");
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				
				
		        throw new NullPointerException();

			}
	    }
	}

	public void example(){
			SparkConf conf = new SparkConf().setAppName("csv2json").setMaster("local[4]");
			JavaSparkContext jsc = new JavaSparkContext(conf);
			

			JdbcRDD<Object[]> jdbcRDD = new JdbcRDD<>(jsc.sc(), new GetConnection(), "select * from employee where eid>=? and eid<=?",
					1, 3, 1,new MapResultObjectArray(),MODULE$.apply(Object[].class) );
			
			
	        JavaRDD<Object[]> javaRDD = JavaRDD.fromRDD(jdbcRDD, MODULE$.apply(Object[].class));

			List<String> employeeFullNameList = javaRDD.map(record -> record[0]+" "+record[1]+" "+record[2]).collect();
	        employeeFullNameList.forEach(System.out::println);

	        JdbcRDD<String> jdbcRDD2 = new JdbcRDD<>(jsc.sc(), new GetConnection(), "select * from employee where eid>=? and eid<=?",
					1, 3, 1,new MapResultString(),MODULE$.apply(String.class) );
			
			
	        JavaRDD<String> javaRDD2 = JavaRDD.fromRDD(jdbcRDD2, MODULE$.apply(String.class));

			List<String> empName = javaRDD2.map(record -> record).collect();

			empName.forEach(System.out::println);
	        
	}	
	
	public static void main(String[] args) {
		JDBCRDDApp app =new  JDBCRDDApp();
		app.example();
	}
}
