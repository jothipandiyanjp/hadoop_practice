package spark.core.example

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object App {

  def main(args : Array[String]) {
    val conf = new SparkConf().setAppName("wordCount").setMaster("local")
    val sc = new SparkContext(conf)
    
     val lines = sc.textFile("src/main/resources/test")
val pairs = lines.map(s => (s.toInt, 1))
val counts = pairs.reduceByKey((a, b) => a + b)
    for( t <- counts.collect() ){
      print(t)
    }
  }

}
