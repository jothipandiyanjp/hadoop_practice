package com.bizruntime.hadoop.option2.compositekey

import org.apache.hadoop.io.{WritableComparable, WritableComparator}

/*
 * Controls which keys are grouped together for a single call to Reducer.reduce() function.
 * 
*/

class DateTemperatureGroupingComparator 
      extends WritableComparator(classOf[DataTemperaturePair],true){

   override def compare(wc1:WritableComparable[_],wc2:WritableComparable[_]) : Int= {
    val pair1 = wc1.asInstanceOf[DataTemperaturePair]
    val pair2 = wc2.asInstanceOf[DataTemperaturePair]
    pair1.yearMonth.compareTo(pair2.yearMonth)       
  }
   
}