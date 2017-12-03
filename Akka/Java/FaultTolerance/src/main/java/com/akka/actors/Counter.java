package com.akka.actors;


import com.akka.actors.helper.CounterApi.UseStorage;

import static com.akka.actors.helper.CounterServiceApi.GetCurrentCount;
import static com.akka.actors.helper.CounterServiceApi.CurrentCount;

import com.akka.actors.helper.Increment;
import com.akka.actors.helper.StorageApi.Entry;
import com.akka.actors.helper.StorageApi.Store;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Counter extends UntypedActor {
    final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    final String key;
    long count;
    ActorRef storage;

    
	public Counter(String key, long initialValue) {
		super();
		this.key = key;
		this.count = initialValue;
	}


	@Override
	public void onReceive(Object msg) throws Exception {
	      log.debug("received message {}", msg);
	      if (msg instanceof UseStorage) {
	    	  storage = ((UseStorage)msg).storage;
	    	  storeCount();
	      }else if(msg instanceof Increment){
	    	  count += ((Increment)msg).n;
	    	  storeCount();
	      }else if(msg.equals(GetCurrentCount))
	    	  getSender().tell(new CurrentCount(key, count), getSelf());
	    	else 
	    		unhandled(msg);
	      
	    	  

	}
    void storeCount() {
    	if (storage != null)
    		storage.tell(new Store(new Entry(key	, count)), getSelf());
    }	
}
