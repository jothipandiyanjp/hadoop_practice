package com.bizruntime.scala


class MongoDB1( val host:String, var port:Int ){

	def this()=this("127.0.0.1" , 27017)

}
