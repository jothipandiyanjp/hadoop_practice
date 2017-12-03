package akka.eventsourcing

import scala.collection.immutable.List
import akka.persistence.PersistentActor
import akka.persistence.SnapshotOffer
import akka.actor.ActorSystem
import akka.actor.Props


case class Command1(data: String)
case class Event1(data: String)
case object Shutdown
case class ExampleState1(events : List[String] = Nil){
  
  def updated(evt: Event): ExampleState1 = copy(evt.data :: events)
  def size: Int = events.length
  override def toString: String = events.reverse.toString()    

}

class ExamplePersistentActor1 extends PersistentActor {
   override def persistenceId: String = "sample-id-1"
   var state = ExampleState()
   def numEvents =  state.size

   def updateState(event: Event): Unit ={
    state = state.updated(event)
   }
   
   val receiveRecover: Receive ={
     case evt: Event => updateState(evt)
     case SnapshotOffer(_, snapShot: ExampleState) => state = snapShot
   }
   
   val receiveCommand: Receive = {
    
    case Command(data)  => 
    
          persist(Event(s"${data}-${numEvents}"))(updateState)
          persist(Event(s"${data}-${numEvents + 1}")) { test =>
              updateState(test)
              context.system.eventStream.publish(test)
              deleteSnapshot(91);
    }
          
    case "snap"  => saveSnapshot(state)
    case "print" => println(state)
    case Shutdown => 
      println("Persistent actor going to shutdown")
      context.stop(self)
    }

}
object PersistenceActorShutdown extends App{
    val system = ActorSystem("example")
      val persistentActor = system.actorOf(Props[ExamplePersistentActor1], "persistentActor-4-scala")
      persistentActor.tell(Command("foo"),null)
      persistentActor ! Command("baz")
      persistentActor ! Command("bar")
      persistentActor ! "snap"
      persistentActor ! Command("buzz")
      persistentActor ! "print"
      
      /** 
       *  To shutdown persistent actor send message like this (create one custom class and send as persistentActor ! Shutdown
       *  not as persistentActor ! Poisonpill),
       *  because then incoming commands are stashed while persistence actor is wating 
       *  for confirmation from journal that events have been written when persist() called. 
       *  Since Incoming commands were put into internal-stash  and actor may receive Posion pill and 
       *  and autohandle the Poison pill before processing other messages.
       */
      
      persistentActor ! Shutdown

      
      Thread.sleep(2000)
      system.terminate()

}