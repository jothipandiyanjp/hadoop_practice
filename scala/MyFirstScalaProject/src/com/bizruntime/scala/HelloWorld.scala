package com.bizruntime.scala




class MongoDB( val host:String, var port:Int )


object A {
    def main(args: Array[String]) {
         val client = new MongoDB("127.0.0.1",124);
         println(client);
         println("client port -> "+client.port);
         
         println("client host -> "+client.host);
    }
}  