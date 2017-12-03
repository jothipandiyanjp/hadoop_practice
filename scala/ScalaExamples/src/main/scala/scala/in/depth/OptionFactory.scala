package scala.in.depth

import org.slf4j.LoggerFactory

class OptionFactory{}
object OptionFactory {

    var log=LoggerFactory.getLogger(classOf[OptionFactory])

      def main(args: Array[String]): Unit = {
        var x: Option[String] = Option(null)
        
        log.debug("x => "+x)
        
         x.getOrElse("Initialized")
        
        log.debug("x -> "+x.getOrElse("Initialized"))
        
      }
}