package com.graphx.sample

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.graphx.Edge
import org.apache.spark.graphx.Graph

object EdgeTripletApp {
  def createGraph={

      val conf = new SparkConf().setAppName("GraphApp").setMaster("local[2]")
    
      val sc = new SparkContext(conf)

      val users: RDD[(Long,(String,String))] 
          = sc.parallelize(Array( (3L, ("jp","emp")), (7L, ("Naren","senior")) ,
                              (5L, ("chinna","frnd")), (2L, ("Rich","genius"))))
    
      val relationships : RDD[Edge[String]] 
          = sc.parallelize(Array( Edge(3L, 7L, "collab"), 
                  Edge(5L, 3L,"advisor"), Edge(2L,5L,"prof"), Edge(5L,7L,"prof")))
          
      val defaultUser = ("John" , "missing")
      
      
      val graph = Graph(users, relationships, defaultUser)
      
    //  graph.vertices.collect().foreach(println)
    //  graph.edges.collect().foreach(println)
        
        // filter vertices
        val count = graph.vertices.filter{case (id, (name,pos) )=> pos =="senior"}.count
        println("Vertex Filtered count2 : "+count)
        // filter edges
        val edgeCount = graph.edges.filter { e => e.srcId > e.dstId }.count()
        println("Edge Filtered and count : "+edgeCount)
        
       val edgeCount2 = graph.edges.filter { case Edge(src, dst, prop) => src > dst}.count
        println("Edge Filtered count2 : "+edgeCount2)
        
       val facts: RDD[String] = 
         graph.triplets.map(triplet => triplet.srcAttr._1 +" is the "+ triplet.attr+" of "+triplet.dstAttr._1 )
         
         facts.foreach(println)
        
  }
  def main(args: Array[String]): Unit = {
  EdgeTripletApp.createGraph
  }
}