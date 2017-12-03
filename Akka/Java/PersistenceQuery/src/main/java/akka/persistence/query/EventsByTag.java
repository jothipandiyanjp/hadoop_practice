package akka.persistence.query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.akka.eventsourcing.Event;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.query.journal.leveldb.javadsl.LeveldbReadJournal;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;


public class EventsByTag {
	ActorSystem system = ActorSystem.create("akka1");

	final LoggingAdapter log = Logging.getLogger(system, EventsByTag.class);

	public void getReadJournal() {

		final ActorMaterializer mat = ActorMaterializer.create(system);

		LeveldbReadJournal queries = PersistenceQuery.get(system)
				.getReadJournalFor(LeveldbReadJournal.class,
						LeveldbReadJournal.Identifier());

		Source<EventEnvelope, NotUsed> buzz = queries.eventsByTag("buzz", 0L);
		
		@SuppressWarnings("unchecked")
		final Future<List<Object>> top10BuzzThings =(Future<List<Object>>) buzz.map(t -> t.event()).take(10)
				.runFold(new ArrayList<>(10), (acc, e) -> {
					
					log.debug(((Event)e).getData());
					
					acc.add(e);
					return acc;
				}, mat);					
	}

	public static void main(String[] args) {
		EventsByTag jour = new EventsByTag();
		jour.getReadJournal();
	}

}
