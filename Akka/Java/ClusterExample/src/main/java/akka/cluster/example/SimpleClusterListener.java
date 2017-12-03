package akka.cluster.example;

import java.util.Arrays;

import akka.actor.Address;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class SimpleClusterListener extends UntypedActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().system(),this);

	Cluster cluster = Cluster.get(getContext().system());
	
	@Override
	public void preStart() throws Exception {
//		cluster.joinSeedNodes(Arrays.asList(new Address("akka.tcp://ClusterSystem@127.0.0.1:2551", getContext().system().toString())));
		cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), MemberEvent.class,
				UnreachableMember.class);
	}
	@Override
	public void postStop() throws Exception {
		log.info("UnSubscribed..");
		cluster.unsubscribe(getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof MemberUp) {
			MemberUp memUp = (MemberUp) message;
			log.info("Member is up : " + memUp);
		} else if (message instanceof UnreachableMember) {
			UnreachableMember memUnReachable = (UnreachableMember) message;
			log.info("Member is unreachable : " + memUnReachable);
		} else if (message instanceof MemberRemoved) {
			MemberRemoved memRemoved = (MemberRemoved) message;
			log.info("Member is removed : " + memRemoved);
		} else if (message instanceof MemberEvent) {
		} else if(message instanceof String){
			log.info(""+message);
		}else {
		
			
			unhandled(message);
		}

	}
}
