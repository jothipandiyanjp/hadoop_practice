package com.bizruntime.spark.option1.inmemory

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.log4j.Logger
import org.apache.log4j.Level

object SecondarySort extends Serializable {

  def sortValuesByKey(args: Array[String]) {
    // STEP-1: read input parameters and validate them
    val inputPath = args(0)
    val outputPath = args(1)
    Logger.getLogger("org").setLevel(Level.OFF)

    // STEP-2: Connect to the Sark master by creating JavaSparkContext object  
    val conf = new SparkConf().setAppName("secondarySort").setMaster("local[2]")
    val ctx = new SparkContext(conf)
    
    // Step -3 create rdd
    var lines: RDD[String] = ctx.textFile(inputPath, 1)
    lines.collect()
    var pairs = lines.map { s =>
      val tokens = s.split(",")
      (tokens(0), (tokens(1).toInt, tokens(2).toInt))
    }

    var output = pairs.collect()

    for (tuple <- output) {
      val timevalue = tuple._2
      println(tuple._1 + " , " + timevalue._1 + " , " + timevalue._2)
    }

    var groups = pairs.groupByKey();
    var output2 = groups.collect()

    for (tuple <- output2) {
      println("key : " + tuple._1)
      val list = tuple._2
      for (tuple2 <- list) println("values :" + tuple2._1 + " , " + tuple2._2)
    }
    
    println("==========================")
    val sorted = groups.mapValues { s => s.toList.sorted }
    val output3 = sorted.collect()
    for (tuple <- output3) {
      println("key : " + tuple._1 + " sorted values")
      val list = tuple._2
      for (tuple2 <- list) println("values :" + tuple2._1 + " , " + tuple2._2)
    }
  }

  def main(args: Array[String]): Unit = {
    SecondarySort.sortValuesByKey(args)
  }
}