package akka.eventsourcing

import scala.collection.immutable.List
import akka.persistence.SnapshotOffer
import akka.actor.ActorSystem
import akka.actor.Props
import akka.persistence.PersistentActor
import akka.persistence.journal.leveldb.SharedLeveldbStore
import akka.persistence.journal.leveldb.SharedLeveldbJournal


case class Command(data: String)
case class Event(data: String)

case class ExampleState(events : List[String] = Nil){
  
  def updated(evt: Event): ExampleState = copy(evt.data :: events)
  def size: Int = events.length
  override def toString: String = events.reverse.toString()    

}

class ExamplePersistentActor extends PersistentActor {
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
   
    val store = context.system.actorOf(Props[SharedLeveldbStore], "store")
         SharedLeveldbJournal.setStore(store, context.system)

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
    
    }
   
}
object PersistenceActorExample extends App{
    val system = ActorSystem("example")
   
      val persistentActor = system.actorOf(Props[ExamplePersistentActor], "persistentActor-4-scala")
      persistentActor.tell(Command("foo"),null)
      persistentActor ! Command("baz")
      persistentActor ! Command("bar")
      persistentActor ! "snap"
      persistentActor ! Command("buzz")
      persistentActor ! "print"
      Thread.sleep(1000)
   //   system.terminate()

}