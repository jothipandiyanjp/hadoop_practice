package akka.cluster.stats.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.stats.StatsMessages.*;
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope;
import akka.routing.FromConfig;

public class StatsService extends UntypedActor {
		
	ActorRef workerRouter = getContext().actorOf(FromConfig.getInstance().props(Props.create(StatsWorker.class)),"workerRouter");

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof StatsJob){
		      StatsJob job = (StatsJob) message;
		      if (job.getText().equals("")) {
		        unhandled(message);
		      } else {
		          final String[] words = job.getText().split(" ");
		          final ActorRef replyTo = getSender();	
		          
		          ActorRef aggregator = getContext().actorOf(Props.create(StatsAggregator.class, words.length, replyTo));

		          for(String word : words)
		        	  workerRouter.tell(new ConsistentHashableEnvelope(word , word ), aggregator);
		          
		      }
			
		}else
			unhandled(message);
	}
}
