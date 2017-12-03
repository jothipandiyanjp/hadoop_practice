package com.bizruntime.hadoop.option2.compositekey

import org.apache.hadoop.io.Writable

import java.io.DataInput
import java.io.DataOutput
import org.apache.hadoop.io.WritableComparable
import org.apache.hadoop.io.Text
import org.apache.hadoop.io.IntWritable
import java.io.IOException

class DataTemperaturePair(var yearMonth: Text,var day: Text,var temperature: IntWritable)
    extends Writable with WritableComparable[DataTemperaturePair] {

  def this() = this(null, null, null)

  @throws(classOf[IOException])
  override def readFields(in: DataInput) = {
    println("ReadFileds "+ in)
    yearMonth.readFields(in)
    day.readFields(in)
    temperature.readFields(in)
  }

  @throws(classOf[IOException])
  override def write(out: DataOutput) = {
    println("Write "+ out)
    yearMonth.write(out)
    day.write(out)
    temperature.write(out)

  }

  override def compareTo(pair: DataTemperaturePair): Int = {
    var compareValue = yearMonth.compareTo(pair.yearMonth)
    if (compareValue == 0) compareValue = temperature.compareTo(pair.temperature)

    //     compareValue      // ascending order
    -1 * compareValue // descending  order
  }

  override def equals(x:Any): Boolean = {    
    if (this==x)
      true
    if(x == null || getClass != x.getClass())
      false
    
    val dataTemperaturePair:DataTemperaturePair = x.asInstanceOf[DataTemperaturePair]
    
    if(temperature !=null )
      if(!temperature.equals(dataTemperaturePair.temperature) || dataTemperaturePair.temperature != null)
        false
    if(yearMonth !=null )
      if(!yearMonth.equals(dataTemperaturePair.yearMonth) || dataTemperaturePair.yearMonth != null)
        false
        
      true
  }
  override def hashCode(): Int = {
    var hash = (year: Any) => if (year != null) year.hashCode().intValue() else 0
    var result: Int = hash.apply(yearMonth)
    result = (31 * result) + hash.apply(temperature)
    result
  }

  override def toString()={
    "yearMonth= "+yearMonth+" day= "+day+" temperature= "+temperature
  }
}
