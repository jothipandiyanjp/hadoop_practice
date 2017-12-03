package spark.core.example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class WordCount implements Serializable{
	
	

	public  void wordCountJava7( String filename )
    {
        SparkConf conf = new SparkConf().setAppName("Java_wordCount").setMaster("spark://192.168.1.193:7077");
        
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> input = sc.textFile( filename );
        
		JavaRDD<String> words = input.flatMap(
                new FlatMapFunction<String, String>() {
                    public Iterable<String> call(String s) {
                        return Arrays.asList(s.split(" "));
                    }
                } );

        // Java 7 and earlier: transform the collection of words into pairs (word and 1)
		JavaPairRDD<String, Integer> counts = words.mapToPair(
            new PairFunction<String, String, Integer>(){
                @SuppressWarnings({ "unchecked", "rawtypes" })
				public Tuple2<String, Integer> call(String s){
                        return new Tuple2(s, 1);
                    }
            } );

        // Java 7 and earlier: count the words
		JavaPairRDD<String, Integer> reducedCounts = counts.reduceByKey(
            new Function2<Integer, Integer, Integer>(){
                public Integer call(Integer x, Integer y){ return x + y; }
            } );

        // Java 7 and earlier: transform the collection of words into pairs (word and 1) and then count them
        
		JavaPairRDD<String, Integer> counts1 = words.mapToPair(
                new PairFunction<String, String, Integer>(){
                    @SuppressWarnings({ "unchecked", "rawtypes" })
					public Tuple2<String, Integer> call(String s){
                        return new Tuple2(s, 1);
                    }
                } ).reduceByKey(
                new Function2<Integer, Integer, Integer>(){
                    public Integer call(Integer x, Integer y){ return x + y;}
                } );
        

        // Save the word count back out to a text file, causing evaluation.
//        reducedCounts.saveAsTextFile( "hdfs://192.168.1.193:9000/output/spark1" );
        List<Tuple2<String, Integer>> output = counts.collect();
		
		for(Tuple2<String, Integer> tuple : output)
			System.out.println("tuple -> "+tuple);		
    }

    /*public static void wordCountJava8( String filename )
    {
        // Define a configuration to use to interact with Spark
        SparkConf conf = new SparkConf().setMaster("local").setAppName("Work Count App");

        // Create a Java version of the Spark Context from the configuration
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Load the input data, which is a text file read from the command line
        JavaRDD<String> input = sc.textFile( filename );

        // Java 8 with lambdas: split the input string into words
        JavaRDD<String> words = input.flatMap( s -> Arrays.asList( s.split( " " ) ) );

        // Java 8 with lambdas: transform the collection of words into pairs (word and 1) and then count them
        JavaPairRDD<String, Integer> counts = words.mapToPair( t -> new Tuple2( t, 1 ) ).reduceByKey( (x, y) -> (int)x + (int)y );

        // Save the word count back out to a text file, causing evaluation.
        counts.saveAsTextFile( "output" );
    }*/

    public static void main( String[] args )
    {
    	WordCount wd = new WordCount();
        if( args.length == 0 )
        {
            System.exit( 0 );
        }
       wd.wordCountJava7( args[ 0 ] );
       // wordCountJava8( args[ 0 ] );
    }
}