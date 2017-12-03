package akka.persistence.query;

import java.util.HashSet;
import java.util.Set;

import com.akka.eventsourcing.Event;

import akka.persistence.journal.EventAdapter;
import akka.persistence.journal.EventSeq;
import akka.persistence.journal.Tagged;
import akka.persistence.journal.WriteEventAdapter;

public class MyTaggingEventAdapter implements WriteEventAdapter{

	@Override
	public String manifest(Object arg0) {
		return "";
	}

	
	public EventSeq fromJournal(Object arg0, String arg1) {

		return EventSeq.empty();
	}
	@Override
	public Object toJournal(Object event) {
		if (event instanceof Event) {
			Event evt = (Event) event;

			Set<String> tags = new HashSet<String>();
			if (evt.getData().contains("foo")) 
				tags.add("foo");
			else if ( evt.getData().contains("bar")) 
				tags.add("bar");
			else if (evt.getData().contains("buzz")) 
				tags.add("buzz");
			
			if(tags.isEmpty()){
				return event;
			}
			else{
				return new Tagged(event, tags);
			}
			
		}
		return event;
	}
	
	
}
