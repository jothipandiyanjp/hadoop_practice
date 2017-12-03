package akka.persistence.query;

import com.akka.eventsourcing.Event;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.query.EventEnvelope;
import akka.persistence.query.journal.leveldb.javadsl.LeveldbReadJournal;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;


public class MyReadJournalProvider {
	ActorSystem system = ActorSystem.create("akka1");

	final LoggingAdapter log = Logging.getLogger(system, MyReadJournalProvider.class);

	
	public void getReadJournal(){
		
		final ActorMaterializer mat = ActorMaterializer.create(system);
		
		
		
		LeveldbReadJournal queries = PersistenceQuery.get(system)
				 							.getReadJournalFor(LeveldbReadJournal.class,
			 													LeveldbReadJournal.Identifier());
		
	//	Source<String, NotUsed> s = queries.allPersistenceIds();
		
		
		Source<EventEnvelope, NotUsed> source = queries.eventsByPersistenceId("sample-id-1", 0, Long.MAX_VALUE);
			source.runForeach(event ->{
				            Event event1 = (Event)event.event();
				            log.debug(event1.getData());
			}, mat);
			
	}
	public static void main(String[] args) {		
		MyReadJournalProvider jour = new MyReadJournalProvider();
		jour.getReadJournal();
	}
}
