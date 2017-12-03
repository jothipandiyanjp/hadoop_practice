package com.bizruntime.topn.yarn

import org.apache.hadoop.conf.Configured
import org.apache.hadoop.util.Tool
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.util.ToolRunner
import org.apache.log4j.Logger
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat
import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.fs.Path
import org.apache.hadoop.conf.Configuration
import java.io.FileSystem
import org.apache.hadoop.fs.FileSystem


class TopNDriver extends Configured with Tool{

private val logger = Logger.getLogger(classOf[TopNDriver])

  override def run(args: Array[String]): Int = {
    var job = new Job(getConf)

    //       HadoopUtil.addJarsToDistributedCache(job, "/lib/");

    val N = args(0).toInt
    job.getConfiguration().setInt("N", N)
    job.setJobName("TopNlist")

    /*    job.setInputFormatClass(classOf[SequenceFileInputFormat])
    job.setOutputFormatClass(classOf[SequenceFileOutputFormat)
*/
     job.getConfiguration().set("mapreduce.framework.name", "local");

    job.setMapperClass(classOf[TopNMapper]);
    job.setReducerClass(classOf[TopNReducer]);
    job.setNumReduceTasks(1);

      // map()'s output (K,V)
      job.setMapOutputKeyClass(classOf[NullWritable]);   
      job.setMapOutputValueClass(classOf[Text]);   
      
      
            // reduce()'s output (K,V)
      job.setOutputKeyClass(classOf[IntWritable]);
      job.setOutputValueClass(classOf[Text]);

      FileInputFormat.setInputPaths(job, new Path(args(1)));
      FileOutputFormat.setOutputPath(job, new Path(args(2)));
      val status = job.waitForCompletion(true)
      if(status ) 0 else 1
  }

  def submitJob(args: Array[String]) {
    if (args.length != 3) {
      logger.warn("usage TopNDriver <N> <input> <output>");
      System.exit(1)
    }

    val returnStaus = ToolRunner.run(new TopNDriver, args)

  }

}