package com.akka.actors;

import static com.akka.actors.helper.CounterServiceApi.GetCurrentCount;
import static com.akka.actors.helper.CounterApi.UseStorage;
import static com.akka.actors.helper.StorageApi.Get;

import java.util.ArrayList;
import java.util.List;

import com.akka.actors.helper.Increment;
import com.akka.actors.helper.StorageApi.Entry;
import com.akka.custom.exception.ServiceUnavailable;
import com.akka.custom.exception.StorageException;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.stop;
import static akka.actor.SupervisorStrategy.restart;
import scala.Option;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.dispatch.sysmsg.Terminate;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;

public class CounterService extends UntypedActor {

	private static class SenderMsgPair {
		final ActorRef sender;
		final Object msg;

		SenderMsgPair(ActorRef sender, Object msg) {
			this.msg = msg;
			this.sender = sender;
		}

	}
    static final Object Reconnect = "Reconnect";
	final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    final List<SenderMsgPair> backlog = new ArrayList<SenderMsgPair>();

	final String key = getSelf().path().name();
	ActorRef storage;
	ActorRef counter;

	private static SupervisorStrategy strategy = new OneForOneStrategy(3,
			Duration.create("5 seconds"), new Function<Throwable, Directive>() {
				public Directive apply(Throwable t) {
					if (t instanceof StorageException) {
						return restart();
					} else {
						return escalate();
					}
				}
			});

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}

	@Override
	public void preRestart(Throwable reason, Option<Object> message)
			throws Exception {

		initStorage();
	}

	void initStorage() {
		storage = getContext().watch(getContext().actorOf(Props.create(Storage.class),"storage"));

		if(counter != null)
			counter.tell(new UseStorage(storage), getSelf());
	
		storage.tell(new Get(key), getSelf());
		
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		log.debug("received message ",msg );
	      if (msg instanceof Entry && ((Entry) msg).key.equals(key) &&
	    	        counter == null) {
	         final long value = ((Entry) msg).value;
	         counter = getContext().actorOf(Props.create(Counter.class,key,value));
	          
	        counter.tell(new UseStorage(storage), getSelf());
	        
	        for(SenderMsgPair each : backlog)
	        	counter.tell(each.msg, each.sender);
	        
	        backlog.clear();
	     }else if(msg instanceof Increment)
	    	 forwardOrPlaceInBacklog(msg);
	     else if(msg.equals(GetCurrentCount))
	    	 forwardOrPlaceInBacklog(msg);
	     else if(msg instanceof Terminated){
	    	 storage = null;
	    	 counter.tell(new UseStorage(null), getSelf());
	    	 getContext().system().scheduler().scheduleOnce(
	    	            Duration.create(10, "seconds"), getSelf(), Reconnect,
	    	            getContext().dispatcher(), null);
	     }else if(msg.equals(Reconnect))
	    	 initStorage();
	     else
	    	 unhandled(msg);
	}

	final int MAX_BACKLOG = 10000;

	public void forwardOrPlaceInBacklog(Object msg){
		
		if (counter == null) {
	        if (backlog.size() >= MAX_BACKLOG)
	          throw new ServiceUnavailable("CounterService not available," +
	            " lack of initial value");
	        backlog.add(new SenderMsgPair(getSender(), msg));
	      } else {
	        counter.forward(msg, getContext());
	      }
	}
}
