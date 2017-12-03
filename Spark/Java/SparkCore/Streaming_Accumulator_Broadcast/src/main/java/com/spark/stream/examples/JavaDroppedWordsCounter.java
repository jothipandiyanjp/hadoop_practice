package com.spark.stream.examples;

import org.apache.spark.Accumulator;
import org.apache.spark.api.java.JavaSparkContext;

//   * Use this singleton to get or register an Accumulator.

public class JavaDroppedWordsCounter {
	  private static volatile Accumulator<Integer> instance = null;
	  public static Accumulator<Integer> getInstance(JavaSparkContext jsc) {
		  if (instance == null) {
		      synchronized (JavaDroppedWordsCounter.class) {
		        if (instance == null) {
		          instance = jsc.accumulator(0, "WordsInBlacklistCounter");
		        }
		      }
		    }
		    return instance;  
	  }
}
