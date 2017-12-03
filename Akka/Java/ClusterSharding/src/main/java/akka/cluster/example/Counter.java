package akka.cluster.example;


import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.PoisonPill;
import akka.actor.ReceiveTimeout;
import akka.cluster.sharding.ShardRegion;
import akka.japi.Procedure;
import akka.persistence.UntypedPersistentActor;

public class Counter extends UntypedPersistentActor {
	
	public static enum CounterOp{
		INCREMENT, DECREMENT
	}
	
	public static class GET implements Serializable{
		final public long counterId;

		public GET(long counterId) {
			super();
			this.counterId = counterId;
		}
		
	}
	
	public static class EntityEnvelope implements Serializable{
		public final long id;
		public final Object payload;
		public EntityEnvelope(long id, Object payload) {
			this.id = id;
			this.payload = payload;
		}
		
	}
	
	public static class CounterChanged implements Serializable{
		public final int delta;

		public CounterChanged(int delta) {
			this.delta = delta;
		}
	}
	
	
	public String persistenceId() {
		return "Counter-"+getSelf().path().name();
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();
		context().setReceiveTimeout(Duration.create(120, TimeUnit.SECONDS));
	}
	
	int count = 0;
	void updateState(CounterChanged event){
		count += event.delta;
	}
	
	@Override
	public void onReceiveRecover(Object message) throws Exception {
	    if (message instanceof CounterChanged){
	    	updateState((CounterChanged)message);
	    }else{
	    	unhandled(message);
	    }
	}
	@Override
	public void onReceiveCommand(Object message) throws Exception {
		if(message instanceof GET){

			getSender().tell(count, getSelf());
		}else if(message == CounterOp.INCREMENT){

			persist(new CounterChanged(+1), new Procedure<CounterChanged>(){
				public void apply(CounterChanged event) throws Exception {
					updateState(event);
				}
				
			});
		}else if(message == CounterOp.DECREMENT){
			persist(new CounterChanged(-1), new Procedure<CounterChanged>(){
				public void apply(CounterChanged event) throws Exception {
					updateState(event);		
				}
			});			
		}else if(message.equals(ReceiveTimeout.getInstance())){
			getContext().parent().tell(new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
		}else{
			unhandled(message);
		}
	}

	
}
