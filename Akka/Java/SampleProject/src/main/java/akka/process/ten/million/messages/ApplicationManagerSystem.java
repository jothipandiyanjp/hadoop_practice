package akka.process.ten.million.messages;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;

public class ApplicationManagerSystem {

	private ActorSystem system;
	private ActorRef router;
	private final static int no_of_msgs = 10 * 1000000;

	public ApplicationManagerSystem() {
		final int no_of_workers = 15;

		system = ActorSystem.create("LoadGeneratorApp");

		final ActorRef appManager = system.actorOf(Props.create(JobControllerActor.class,no_of_msgs), "jobController");
		
		router = system.actorOf(Props.create(WorkerActor.class,appManager).withRouter(new RoundRobinPool(no_of_workers)));
	}
	
	private void generateLoad() {
		for (int i = no_of_msgs; i >= 0; i--) {
			router.tell("Job Id " + i + "# send",null);
		}

	}

	
	public static void main(String[] args) {
		new ApplicationManagerSystem().generateLoad();
	}
}
