package com.graphx.example

import org.apache.spark.graphx.GraphLoader

object ReverseObject {
  def main(args: Array[String]): Unit = {
    GraphLoader.edgeListFile
    new ReverseApp().reversingVertices()
  }
}