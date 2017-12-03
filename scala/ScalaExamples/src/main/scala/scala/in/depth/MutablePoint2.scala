package scala.in.depth

import scala.collection.immutable.HashMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MutablePoint2(var x : Int, var y : Int)  extends Equals{
  
  def mov(mx : Int , my : Int):Unit={
    x = mx + x
    y = my + y
  }
    
  override def hashCode():Int = x + (31 * x)
  
  def canEqual(that : Any ) :Boolean = that match  {
    case p : MutablePoint2 => true
    case _  => false
  }
  
  override def equals(that : Any) : Boolean ={
      def strictEquals(other: MutablePoint2 )= this.x == other.x && this.y == other .y

      that match{
          case a: AnyRef if this eq a => true
          case p: MutablePoint2 => (p canEqual this) && strictEquals(p)
          case _ => false
        }
      
  }
  
  
}

object MutablePoint2{
  var log=LoggerFactory.getLogger(classOf[MutablePoint2])
  
  def main(args: Array[String]): Unit = {
    val x = new MutablePoint2(1,2);
    
    val y = new MutablePoint2(1,3);
    
    val z = new MutablePoint2(1,2);
    
    log.debug("x == y ==> " + (x == y))
    log.debug("x == z ==> "+ (x == z))

    val map = HashMap (x -> "Hai" , y -> "Hello" )
    
    log.debug(map(x))
    
     log.debug("x.mov(1, 1) ==> "+x.mov(1, 1))
    
    log.debug(map(y))
    
     log.debug("map.find(_._1 == x) ==> "+map.find(_._1 == x))
   // println(map(z))
    
    
    
  }
}