package akka.cluster.ddata.example;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.cluster.Cluster;
import akka.cluster.ddata.DistributedData;
import akka.cluster.ddata.Key;
import akka.cluster.ddata.ORSet;
import akka.cluster.ddata.ORSetKey;
import akka.cluster.ddata.Replicator;
import akka.cluster.ddata.Replicator.Changed;
import akka.cluster.ddata.Replicator.Subscribe;
import akka.cluster.ddata.Replicator.Update;
import akka.cluster.ddata.Replicator.UpdateResponse;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class DataBot extends AbstractActor {

	// message to the actor itself
	private static String TICK = "TICK";
	
	private final LoggingAdapter log = Logging.getLogger(context().system(), this);

	// Replicator Actor
	private final ActorRef replicator = DistributedData.get(context().system()).replicator();

	private final Cluster node = Cluster.get(context().system());

	
	public DataBot() {
		receive(ReceiveBuilder
					.match(String.class, a -> a.equals(TICK), a -> receiveTick())
					.match(Changed.class, c -> c.key().equals(dataKey), c -> receiveChanged((Changed<ORSet<String>>)c))
					.match(UpdateResponse.class, r -> receiveUpdateResponse())
					.build()
				);
	}	
	
	
	  private final Key<ORSet<String>> dataKey = ORSetKey.create("key");

	
	private void receiveTick(){

		
		// Taking random value between 97 to 123 and converting to char
		String s = String.valueOf((char) ThreadLocalRandom.current().nextInt(97, 123));
	    
		/*
		 * ThreadLocalRandom.current().nextBoolean() gives random true or false value
		 * if true add an element to replicator actor
		 * or remove an element from replicator actor
		 */
		if (ThreadLocalRandom.current().nextBoolean()) {
	        log.info("Adding: {}", s);
	        Update<ORSet<String>> update = new Update<>(dataKey, 
	                ORSet.create(), 
	                Replicator.writeLocal(), 
	                curr ->  curr.add(node, s));
	        replicator.tell(update, self());
	    }else{
	        log.info("Removing: {}", s);
	        Update<ORSet<String>> update = new Update<>(
	                dataKey, 
	                ORSet.create(), 
	                Replicator.writeLocal(), 
	                curr ->  curr.remove(node, s));
	            replicator.tell(update, self());
	          
	    }
	}
	
	// Onelement changed this method will be called
	// Observed Remove Set (ORSet)
	  private void receiveChanged(Changed<ORSet<String>> c) {
		  
		    ORSet<String> data = c.dataValue();
		    log.info("Current elements: {}", data.getElements());
	  }
	  
	  private void receiveUpdateResponse() {
	  }
	  
	@Override
	public void preStart() throws Exception {
	    Subscribe<ORSet<String>> subscribe = new Subscribe<>(dataKey, self());
	    replicator.tell(subscribe, ActorRef.noSender());

	}
	
	
	// SHeduling a msg (TICK) to replicator actor for every 5sec
	  private final Cancellable tickTask = context().system().scheduler().schedule(
		      Duration.create(5, TimeUnit.SECONDS), Duration.create(5, TimeUnit.SECONDS), self(), TICK,
		      context().dispatcher(), self());
	
	@Override
	public void postStop() throws Exception {		
	    tickTask.cancel();
	}
}
