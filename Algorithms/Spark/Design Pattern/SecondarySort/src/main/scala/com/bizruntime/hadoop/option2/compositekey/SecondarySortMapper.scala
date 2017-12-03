package com.bizruntime.hadoop.option2.compositekey

import org.apache.hadoop.mapreduce.Mapper

import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text

class SecondarySortMapper 
    extends Mapper[LongWritable, Text, DataTemperaturePair, Text]{
    
  private var theTemperature = new Text
  private var pair = new DataTemperaturePair
  
  override  def map(key: LongWritable, value: Text, 
        context: Mapper[LongWritable, Text, DataTemperaturePair, Text]#Context)={
    
    var line = value.toString()
    var tokens:Array[String] = line.split(" ")
    
    var yearMonth = tokens(0) + tokens(1)
    var day = tokens(2)
    val temperature = tokens(3).toInt
    
    pair.yearMonth.set(yearMonth)
    pair.day.set(day)
    pair.temperature.set(temperature)
    
    theTemperature.set(tokens(3))

    // composite key contains yearmonth (natural key) and temperature 
    //  value contains only temperature
    
    context.write(pair, theTemperature)
  }
}