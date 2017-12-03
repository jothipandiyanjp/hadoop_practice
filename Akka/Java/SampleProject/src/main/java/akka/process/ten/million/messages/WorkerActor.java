package akka.process.ten.million.messages;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import scala.concurrent.duration.Duration;


public class WorkerActor extends UntypedActor {

	private ActorRef jobController;

	@Override
	public void onReceive(Object message) throws Exception {
		getContext().system()
		.scheduler()
		.scheduleOnce(Duration.create(1000, TimeUnit.MILLISECONDS),
				jobController, "Done",getContext().dispatcher(),null);
	}

	public WorkerActor(ActorRef inJobController) {
		jobController = inJobController;
	}
}
