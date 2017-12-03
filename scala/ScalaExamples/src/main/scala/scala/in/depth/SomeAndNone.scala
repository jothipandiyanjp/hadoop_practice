package scala.in.depth

import org.slf4j.LoggerFactory

class SomeAndNone {
 
  
}

object SomeAndNone{
    var log=LoggerFactory.getLogger(classOf[SomeAndNone])

  def main(args: Array[String]): Unit = {
    
    var x: Option[String] = None
    
   // log.debug(x.get)
    
    log.debug(x.getOrElse("default"))
    
    x = Some("Initialized ")
 
    log.debug(x.get)
    
    log.debug(x.getOrElse("default"))
    
  }
}