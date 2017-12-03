package com.bizruntime.hadoop.option2.compositekey

import org.apache.hadoop.mapreduce.Partitioner
import org.apache.hadoop.io.Text
import scala.math._
/*
 * Using natural key(yearMonth) only we are partitioning . 
 * Partitioner decides which mapper output goes to which reduer based on mapper output key
 * 
 * example year 1999 goes to on reduce and 2000 goes to another reducer
 * 
*/
class DataTemperaturePartitioner extends Partitioner[DataTemperaturePair,Text]{  
  override def getPartition(pair:DataTemperaturePair, 
      text: Text, numOfPartitions: Int) : Int =  abs(pair.yearMonth.hashCode() % numOfPartitions)   
}