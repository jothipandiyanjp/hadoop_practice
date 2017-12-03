package com.bizruntime.hadoop.option2.compositekey

import org.apache.hadoop.conf.Configured
import org.apache.hadoop.util.Tool
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.log4j.Logger
import org.apache.hadoop.util.ToolRunner

class SecondarySortDriver extends Configured with Tool{
  private val logger = Logger.getLogger(classOf[SecondarySortDriver])
  
  override def run(args:Array[String]):Int  = {
      var conf = getConf
      var job = new Job(conf)
      job.setJarByClass(classOf[SecondarySortDriver])
      job.setJobName("SecondarySortDriver")
      
      FileInputFormat.setInputPaths(job, new Path(args(0)))
      FileOutputFormat.setOutputPath(job, new Path(args(1)))
      
      job.setMapperClass(classOf[SecondarySortMapper])
      job.setReducerClass(classOf[SecondarySortReducer])
      job.setPartitionerClass(classOf[DataTemperaturePartitioner])
      job.setGroupingComparatorClass(classOf[DateTemperatureGroupingComparator])

      var status = job.waitForCompletion(true)
      logger.info("run() status: "+status)
      if(status) 0 else 1
  }
 
  /*
   *  
   * 
  */
  def submitJob(args:Array[String]){
        ToolRunner.run(new SecondarySortDriver, args)     
  }
 
  def main(args: Array[String]): Unit = {
    val job = new SecondarySortDriver
    job.submitJob(args)
  }
}