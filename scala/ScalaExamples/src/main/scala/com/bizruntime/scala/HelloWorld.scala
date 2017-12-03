package com.bizruntime.scala

import org.slf4j.Logger;
import org.slf4j.LoggerFactory


class MongoDB( val host:String, var port:Int )

object Test {
 // var log=LoggerFactory.getLogger(classOf[A]);  
  def main(args: Array[String]) {
    
         val client = new MongoDB("127.0.0.1",124);
         println(client);
         println("client port -> "+client.port);         
         println("client host -> "+client.host);

    }
}  