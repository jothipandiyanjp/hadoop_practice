package com.bizruntime.topn.yarn

import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.io.Text
import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.NullWritable
import scala.collection.immutable.TreeMap

class TopNMapper extends Mapper[Text, IntWritable, NullWritable, Text] {

  var N = 10

  private var top = new TreeMap[Int, String]

  // setup() function will be executed once per mapper
  override def setup(ctx: Mapper[Text, IntWritable, NullWritable, Text]#Context) {
    N = ctx.getConfiguration.getInt("N", 10) // 10 is default value         
  }

  def map(key: Text, value: IntWritable, context: Context) {
    val keyAsString = key.toString
    val frequency = value.get
    
    val compositeValue = keyAsString + "," + frequency

    top += (frequency -> compositeValue)
    
    if (top.size > N) top -= top.firstKey
  }

  // cleanup() function will be executed once per mapper
  override def cleanup(ctx: Mapper[Text, IntWritable, NullWritable, Text]#Context) {

    top.foreach{str => ctx.write(NullWritable.get, new Text(str._2))}

//    for (str <- top. values) ctx.write(NullWritable.get, new Text(str))
  }
}