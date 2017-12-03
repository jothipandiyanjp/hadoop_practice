package com.bizruntime.topn.yarn

import org.apache.hadoop.mapreduce.Reducer
import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.Text
import scala.collection.immutable.TreeMap
import java.lang.Iterable
import scala.collection.JavaConversions
import scala.collection.JavaConverters

class TopNReducer extends Reducer[NullWritable, Text, IntWritable, Text] {

  var N = 10

  private var top = new TreeMap[Int, String]

  // setup() function will be executed once per mapper
  override def setup(ctx: Reducer[NullWritable, Text, IntWritable, Text]#Context) {
    N = ctx.getConfiguration.getInt("N", 10) // 10 is default value         
  }

  override def reduce(key: NullWritable, valus: Iterable[Text],
                      context: Reducer[NullWritable, Text, IntWritable, Text]#Context) {
    val values = JavaConversions.iterableAsScalaIterable(valus);
    for (value: Text <- values) {
      var url = value.toString().trim().split(",")
      top += (url(0).toInt -> url(1))
      if(top.size > N) top -= top.firstKey
    
    }
  
    // emit final top N   
    for( (k,v)<-   top)   context.write(new IntWritable(k), new Text(v))
    
  }

}