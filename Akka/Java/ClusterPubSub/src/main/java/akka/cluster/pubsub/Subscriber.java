package akka.cluster.pubsub;


import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Subscriber extends UntypedActor{

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	public Subscriber() {
		ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
		 mediator.tell(new DistributedPubSubMediator.Subscribe("content", getSelf()),getSelf());
 }
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof String){
			log.info("Received Message : "+message);
		} else if(message instanceof DistributedPubSubMediator.SubscribeAck)
			log.info("Subscribing...");
		else
			unhandled(message);
		
	}
}
