package akka.example;

import akka.actor.ActorSelection;
import akka.japi.Function;
import akka.japi.Procedure;
import akka.persistence.UntypedPersistentActorWithAtLeastOnceDelivery;

public class MyPersistentActor extends UntypedPersistentActorWithAtLeastOnceDelivery{

	private ActorSelection destination;
	
	public String persistenceId() {
		return "persistenceId";
	}
	
	  public MyPersistentActor(ActorSelection destination) {
	      this.destination = destination;
	  }
	  
	  
	@Override
	public void onReceiveCommand(Object msg) throws Exception {
		  if (msg instanceof String) {
	            String s = (String) msg;
		      persist(new MsgSent(s), new Procedure<MsgSent>() {
		          public void apply(MsgSent evt) {
		            updateState(evt);
		          }
		        });
		  }else if (msg instanceof Confirm) {
		      Confirm confirm = (Confirm) msg;
		      persist(new MsgConfirmed(confirm.deliveryId), new Procedure<MsgConfirmed>() {
		        public void apply(MsgConfirmed evt) {
		          updateState(evt);
		        }
		      });
		    } else {
		      unhandled(msg);
		    }
	}
	
	@Override
	public void onReceiveRecover(Object event) throws Exception {
	    updateState(event);
	}
	
	void updateState(Object event) {
		 if (event instanceof MsgSent) {
		      final MsgSent evt = (MsgSent) event;
		      deliver(destination, new Function<Long, Object>() {
		        public Object apply(Long deliveryId) {
		          return new Msg(deliveryId, evt.s);
		        }
		      });
		    } else if (event instanceof MsgConfirmed) {

		      final MsgConfirmed evt = (MsgConfirmed) event;
		      confirmDelivery(evt.deliveryId);
		    }
	  }
	
}
