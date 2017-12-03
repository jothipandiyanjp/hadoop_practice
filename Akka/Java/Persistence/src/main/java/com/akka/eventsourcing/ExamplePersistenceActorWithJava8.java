package com.akka.eventsourcing;

import java.util.Arrays;

import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import akka.japi.pf.ReceiveBuilder;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.SnapshotOffer;
import akka.persistence.UntypedPersistentActor;

public class ExamplePersistenceActorWithJava8 extends AbstractPersistentActor{
	final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
    private ExampleState state = new ExampleState();

	public String persistenceId() {
		return "sample-id-1";
	}


    public int getNumEvents() {
        return state.size();
    }
	
    public PartialFunction<Object, BoxedUnit> receiveRecover() {
    	return ReceiveBuilder.match(Event.class, state :: update)
    						 .match(SnapshotOffer.class, ss -> state = (ExampleState)ss.snapshot()).build();
    }
    public PartialFunction<Object, BoxedUnit> receiveCommand() {
    	return ReceiveBuilder.match(Command.class, c -> {
    		final String data = c.getData();
    		   final Event evt1 = new Event(data + "-" + getNumEvents());
               final Event evt2 = new Event(data + "-" + (getNumEvents() + 1));
               persistAll(Arrays.asList(evt1,evt2), (Event evt) -> {
            	   state.update(evt);
            	   if (evt.equals(evt2)) {
                       context().system().eventStream().publish(evt);
            	   }
               });
    	}).match(String.class, s -> s.equals("snap"), s -> saveSnapshot(state.copy()))
    	.match(String.class, s -> s.equals("print"), s -> log.info(""+state)).build();
    }
}
