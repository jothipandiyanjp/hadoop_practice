package com.graphx.example

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.graphx.Edge
import org.apache.spark.graphx.Graph

object MapTripletsApp {

  def creatingNewGraph() ={
   val conf = new SparkConf().setAppName("DegreeApp").setMaster("local[2]")
    
    Logger.getLogger("org").setLevel(Level.OFF);
    
    val ctx = new SparkContext(conf);

    val users = ctx.parallelize(Array((1L, 1), (2L, 2), (3L, 3), (4l, 4), (5l, 5)))

    val edges = ctx.parallelize(Array(new Edge(1L, 2L, 1), new Edge(2L, 3L, 1),
                  new Edge(4L, 5L, 1), new Edge(4L, 1L, 1)))

    val graph = Graph(users, edges)

    val users2 = ctx.parallelize(Array((1L, 100),  (2L, 100),
                        (3L, 100),(6l, 100)))

    println("graph : ")
    graph.triplets.collect().foreach(println)
       
    val joinedGraph = graph.outerJoinVertices(users2)((vid,_,degOpt)=> degOpt.getOrElse(10))

        println("joined Graph : ")
    joinedGraph.triplets.collect().foreach(println)
    
    val mapVertices = joinedGraph.mapTriplets(edgeTriplet => 1 / edgeTriplet.srcAttr)
                            .mapVertices((id,_) => 1)
    
    println("Triplets : ")    
    mapVertices.triplets.collect().foreach(println)
    
    
  }
  
    
  def main(args: Array[String]): Unit = {
    creatingNewGraph()
  }
  
}