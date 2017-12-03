package akka.eventsourcing

import akka.persistence.PersistentView
import akka.event.Logging
import akka.event.LoggingAdapter
import akka.actor.ActorSystem
import akka.actor.ActorLogging
import akka.actor.Props
import akka.persistence.Update
import akka.persistence.SnapshotOffer
import akka.actor.UnhandledMessage
import scala.concurrent.Await
import akka.pattern.BackoffSupervisor
import akka.pattern.Backoff
import scala.concurrent.duration.Duration
import akka.persistence.PersistentActor

class PersistentActorView extends PersistentView {
  override val log = Logging.getLogger(context.system, this)
  val log1 = Logging(context.system.eventStream, "my.nice.string")

  override def persistenceId: String = "persistence-view1"
   override def viewId: String ="sample-id-1"
   
  override def receive:Receive = {
     
     case payload: SnapshotOffer  => log.debug("Persistent messages"+payload)
     case message: String => (log1.debug("Update"+message),Update(await = true))
     case message: UnhandledMessage => log1.debug("Unhandled messages"+ message)
     

   }
  

}
object PersistentActorViewExample extends App{
  
   val system = ActorSystem("example1")
//   val persistentActor = system.actorOf(Props[PersistentActorView], "persistentActor-4-scala")
   val props = BackoffSupervisor.props(
  Backoff.onStop(
    Props[PersistentActorView],
    childName = "myActor1",
    minBackoff = Duration(3,"seconds"),
    maxBackoff = Duration(30,"seconds"),
    randomFactor = 0.2))
 val persistentActor =  system.actorOf(props, name = "mySupervisor1")
  
  persistentActor ! Update(await = true)
   persistentActor.tell("Message - A", null)
   persistentActor.tell("Message - A", null)
   persistentActor.tell("Message - A", null)
   persistentActor.tell("Message - A", null)
  
}