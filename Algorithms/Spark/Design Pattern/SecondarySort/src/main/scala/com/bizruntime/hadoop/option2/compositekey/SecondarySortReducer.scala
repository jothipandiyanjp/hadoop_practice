package com.bizruntime.hadoop.option2.compositekey

import org.apache.hadoop.mapreduce.Reducer
import org.apache.hadoop.io.Text
import scala.collection.JavaConversions
import scala.collection.mutable.StringBuilder

class SecondarySortReducer  extends Reducer[DataTemperaturePair, Text, Text, Text]{

    override  def reduce(key: DataTemperaturePair, valus: java.lang.Iterable[Text], 
        context: Reducer[DataTemperaturePair, Text,Text,Text]#Context)={
        
      var builder = new StringBuilder
      var values = JavaConversions.iterableAsScalaIterable(valus)
      for(value <- values){
          builder.append(value.toString())
          builder.append(",")
      }
      context.write(key.yearMonth, new Text(builder.toString()))
    }
    
  
}