package akka.cluster.pubsub;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;

public class Publisher extends UntypedActor {
	
	//Cluster cluster = Cluster.get(getContext().system());
	
	@Override
	public void preStart() throws Exception {
		//cluster.subscribe(getSelf(), MemberEvent.class);

	}
	@Override
	public void postStop() throws Exception {
		//cluster.unsubscribe(getSelf());
	}
	  ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
	@Override
	public void onReceive(Object message) throws Exception {
		 if (message instanceof String) {
		      String in = (String) message;
		      String out = in.toUpperCase();
		     // for(int i=0;i<100;i++){
		      mediator.tell(new DistributedPubSubMediator.Publish("content", out),getSelf());
		     // }
		    } else {
		      unhandled(message);
		    }
	}
	
}
