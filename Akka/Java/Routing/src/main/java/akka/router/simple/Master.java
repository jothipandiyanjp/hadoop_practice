package akka.router.simple;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinGroup;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

public class Master extends UntypedActor {

	Router router;
	
	{
		List<Routee> routees = new ArrayList<Routee>();
		for (int i = 0; i < 5; i++) {
			ActorRef ref = getContext().actorOf(Props.create(Work.class));
			getContext().watch(ref);
			routees.add(new ActorRefRoutee(ref));
		}
		router = new Router(new RoundRobinRoutingLogic(),routees);
	}
	
	public void onReceive(Object msg) throws Exception {
		
		if(msg instanceof Work)
			router.route(msg, getSender());
		else if(msg instanceof Terminated){
			router = router.removeRoutee(((Terminated)(msg)).actor());
			ActorRef ref = getContext().actorOf(Props.create(Work.class));
			getContext().watch(ref);
			router = router.addRoutee(new ActorRefRoutee(ref));
			
		}
	}
	
	
}
