package scala.in.depth

import scala.collection.immutable.HashMap

class Point2 (var x : Int,var y : Int ){
  
  def move(mx : Int, my :Int ): Unit = {
    x = x + mx;
    y = y + my;
    
    
  }
  
  override def hashCode():Int = y + (31 * x) 
  
} 

object Test{
  
  def main(args: Array[String]): Unit = {
    val x = new Point2(1,1)
    println(x)
    println(x.##())
   
    val y = new Point2(1,1)

    println(x==y)
 
    println(y.hashCode())
    
    val map =HashMap(x -> "HAI" , y -> "HELLO")
    
    
    println(map(x))
        println(x.move(1, 1))

    println(map(x))
    println(map(y))
    
    val z = new Point2(1,3)
    
    println(map(z))
 
    
  }
  
}