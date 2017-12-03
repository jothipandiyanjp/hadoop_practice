package com.akka.actors;

import java.util.HashMap;
import java.util.Map;
import com.akka.actors.helper.StorageApi.Entry;
import com.akka.actors.helper.StorageApi.Get;
import com.akka.actors.helper.StorageApi.Store;
import com.akka.custom.exception.StorageException;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Storage extends UntypedActor{

    final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    final DummyDB db = DummyDB.instance;

	@Override
	public void onReceive(Object msg) throws Exception {
		log.debug("received message {}", msg);
	      if (msg instanceof Store) {
	        Store store = (Store) msg;
	        db.save(store.entry.key, store.entry.value);
	      } else if (msg instanceof Get) {
	        Get get = (Get) msg;
	        Long value = db.load(get.key);
	        getSender().tell(new Entry(get.key, value == null ?
	          Long.valueOf(0L) : value), getSelf());
	      } else {
	        unhandled(msg);
	      }

	}
	
	public static class DummyDB {
	    public static final DummyDB instance = new DummyDB();
	    private final Map<String, Long> db = new HashMap<String, Long>();
	 
	    private DummyDB() {
	    }
	 
	    public synchronized void save(String key, Long value) throws StorageException {
	      if (11 <= value && value <= 14)
	        throw new StorageException("Simulated store failure " + value);
	      db.put(key, value);
	    }
	 
	    public synchronized Long load(String key) throws StorageException {
	      return db.get(key);
	    }
	}
}
