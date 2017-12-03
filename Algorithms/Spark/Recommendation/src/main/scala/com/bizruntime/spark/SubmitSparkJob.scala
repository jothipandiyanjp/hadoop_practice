package com.bizruntime.spark

import org.apache.log4j.Logger
import org.apache.spark.launcher.SparkLauncher
import scala.concurrent.impl.Future
import scala.concurrent.impl.Future
import scala.util.Success
import scala.util.Failure
import scala.io.Source
import scala.concurrent.Await

object SubmitSparkJob {

  def main(args: Array[String]): Unit = {
    new SubmitSparkJob().run(args)
  }
}

class SubmitSparkJob {
  val LOGGER = Logger.getLogger(classOf[SubmitSparkJob])

  def run(args: Array[String]) {

    val appResource = ""
    val mainClass = ""

    val spark = new SparkLauncher().setVerbose(true)
      .setSparkHome("/usr/local/spark-1.6.1-bin-hadoop2.6/")
      .setAppResource(appResource)
      .setMainClass(mainClass)
      .setAppName("SparkJob")
      .setMaster("local[2]")
      .setConf(SparkLauncher.DRIVER_MEMORY, "1g")
      .addAppArgs(args: _*)

    val process = spark.launch()

    process.getInputStream
    import scala.concurrent.Future
    import scala.concurrent.ExecutionContext.Implicits.global

    val inputFuture = Future {
      val str = Source.fromInputStream(process.getInputStream()).getLines().mkString
      
    }
    
    val inputError = Future {
      Source.fromInputStream(process.getErrorStream()).getLines().mkString
    }
    import scala.concurrent.duration._
    Await.result(inputFuture, 10 seconds)

    inputFuture.onSuccess{case msg => println(msg)}
    inputFuture.onFailure{case msg => println(msg)}
    inputError.onSuccess{case msg => println(msg)}
    inputError.onFailure{case msg => println(msg)}
    
   LOGGER.debug("Finisjed ")
  }

}