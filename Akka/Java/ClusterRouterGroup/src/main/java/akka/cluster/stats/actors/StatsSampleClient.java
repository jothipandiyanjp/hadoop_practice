package akka.cluster.stats.actors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.ReachabilityEvent;
import akka.cluster.ClusterEvent.ReachableMember;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.cluster.stats.StatsMessages.*;

public class StatsSampleClient extends UntypedActor {

	final String servicePath;

	private final Set<Address> nodes = new HashSet<Address>();

	private final LoggingAdapter log = Logging.getLogger(getContext().system(),this);
	  final Cancellable tickTask;

	  Cluster cluster = Cluster.get(getContext().system());

	public StatsSampleClient(String servicePath) {
		this.servicePath = servicePath;
		log.debug("StatsSampleClient constructor. Servicepath => "
				+ servicePath);
	    FiniteDuration interval = Duration.create(2, TimeUnit.SECONDS);

	    tickTask = getContext()
	            .system()
	            .scheduler()
	            .schedule(interval, interval, getSelf(), "tick",
	                getContext().dispatcher(), null);
	}

	@Override
	public void preStart() throws Exception {
		cluster.subscribe(getSelf(), MemberEvent.class, ReachabilityEvent.class);
	}

	@Override
	public void postStop() throws Exception {
		cluster.unsubscribe(getSelf());
		tickTask.cancel();
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message.equals("tick") && !nodes.isEmpty()) {
			List<Address> nodeList = new ArrayList<Address>(nodes);
			Address address = nodeList.get(ThreadLocalRandom.current().nextInt(
					nodeList.size()));
			ActorSelection service = getContext().actorSelection(
					address + servicePath);
			service.tell(new StatsJob("This text will be analysed by different routers and count no of characters"
							+ " and then return back as integer"), getSelf());
			
		}  else if (message instanceof StatsResult) {
		      StatsResult result = (StatsResult) message;
		      log.debug("Result => "+result);

		    } else if (message instanceof JobFailed) {
		      JobFailed failed = (JobFailed) message;
		      log.debug("Failed => "+ failed);

		    } else if (message instanceof CurrentClusterState) {
		      CurrentClusterState state = (CurrentClusterState) message;
		      nodes.clear();
		      for (Member member : state.getMembers()) {
		        if (member.hasRole("compute") && member.status().equals(MemberStatus.up())) {
		          nodes.add(member.address());
		        }
		      }

		    } else if (message instanceof MemberUp) {     // After joining, Member up event
		      MemberUp mUp = (MemberUp) message;
		      if (mUp.member().hasRole("compute"))
		        nodes.add(mUp.member().address());
		    } else if (message instanceof MemberEvent) {   // member Join event
		      MemberEvent other = (MemberEvent) message;
		      nodes.remove(other.member().address());

		    } else if (message instanceof UnreachableMember) {
		      UnreachableMember unreachable = (UnreachableMember) message;
		      nodes.remove(unreachable.member().address());

		    } else if (message instanceof ReachableMember) {
		      ReachableMember reachable = (ReachableMember) message;
		      if (reachable.member().hasRole("compute"))
		        nodes.add(reachable.member().address());

		    } else {
		      unhandled(message);
		    }
		 	}
}
