package com.graphx.example

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx.Edge
import org.slf4j.LoggerFactory
import org.apache.log4j.Logger
import org.apache.log4j.Level

class DegreeExample {
 //  val log = LoggerFactory.getLogger(classOf[DegreeExample])
     val log = Logger.getLogger(classOf[DegreeExample])

  def findingDegrees = {
    val conf = new SparkConf().setAppName("DegreeApp").setMaster("local[2]")
    
    Logger.getLogger("org").setLevel(Level.OFF);
    
    val ctx = new SparkContext(conf);

    val users = ctx.parallelize(Array((1L, 1), (2L, 2), (3L, 3), (4l, 4), (5l, 5)))

    val edges = ctx.parallelize(Array(new Edge(1L, 2L, 1), new Edge(2L, 3L, 1),
      new Edge(4L, 5L, 1), new Edge(4L, 1L, 1)))

    val graph = Graph(users, edges);
    
    graph.ops.inDegrees.collect().foreach( i => log.info("Indegree "+i))
    graph.ops.outDegrees.collect().foreach( i => log.info("Outdegree "+i))
    graph.ops.degrees.collect().foreach( i => log.info("degree "+i))

  }
}
object DegreeApp {

  def main(args: Array[String]): Unit = {
    new DegreeExample().findingDegrees
  }
}