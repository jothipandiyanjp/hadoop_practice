package com.bizruntime.scala


object ScalaBean {

    def main(args: Array[String]) {
          val client = new MongoDB("127.0.0.1",124);
          println(client.port);
    }
}

class AddressBean(

   var firstName:String,
   var lastName:String,
   var city:String
)

