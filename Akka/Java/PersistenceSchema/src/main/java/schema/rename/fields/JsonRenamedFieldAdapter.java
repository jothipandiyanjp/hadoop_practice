package schema.rename.fields;

import akka.persistence.journal.EventAdapter;
import akka.persistence.journal.EventSeq;


public class JsonRenamedFieldAdapter implements EventAdapter{
	
	public String manifest(Object event) {
		return null;
	}
	
	public Object toJournal(Object event) {
		return null;
	}
	
	public EventSeq fromJournal(Object event, String manifest) {
		return null;
	}
	
}
