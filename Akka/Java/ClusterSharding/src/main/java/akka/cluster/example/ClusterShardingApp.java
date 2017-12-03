package akka.cluster.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.example.Counter.GET;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.cluster.sharding.ShardCoordinator;
import akka.cluster.sharding.ShardRegion;
import akka.cluster.sharding.ShardRegion.MessageExtractor;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ClusterShardingApp {
	
    Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + "2551").withFallback(ConfigFactory.load());
    ActorSystem system = ActorSystem.create("ClusterSystem", config);
	

	private void demonstrateUsage() {

		
		// message extractor for entity identifier and shard identifier
		MessageExtractor messageExtractor = new ShardRegion.MessageExtractor() {
			
			public String entityId(Object message) {
				if(message instanceof Counter.EntityEnvelope)
					return String.valueOf(((Counter.EntityEnvelope)message).id);
				else if(message instanceof Counter.GET)
					return  String.valueOf(((Counter.GET)message).counterId);
				else 					
					return null;
			}
			
			public Object entityMessage(Object message ) {
	               if (message instanceof Counter.EntityEnvelope)
	                    return ((Counter.EntityEnvelope) message).payload;
	               else 
	            	   return message;
			}
			
			public String shardId(Object message) {
                int numberOfShards = 100;
				if(message instanceof Counter.EntityEnvelope){
					long id = ((Counter.EntityEnvelope)message).id;
                    return String.valueOf(id % numberOfShards);
                    
				}
				else if(message instanceof Counter.GET){
					long id = ((Counter.GET)message).counterId;
					return String.valueOf(id % numberOfShards);
				} else 					
					return null;
			}};
		
			/*
			 * configuration properties are read by the ClusterShardingSettings 
			 * when created with a ActorSystem parameter
			 */
	        ClusterShardingSettings settings = ClusterShardingSettings.create(system);

	        /*
	         *  Registering the supported entity types(Counter) with ClusterSharding.start 
	         */

	        ActorRef startedCounterRegion = ClusterSharding.get(system).start("Counter", Props.create(Counter.class),settings,messageExtractor);

	       /*	SHARDREGION ACTOR
	        *   Message to the entities always sent via shardregion  
	        */
	        
	        ActorRef counterRegion = ClusterSharding.get(system).shardRegion("Counter");
	        
	        // Counter 
	        counterRegion.tell(new Counter.EntityEnvelope(123,Counter.CounterOp.INCREMENT), null);
	        
	        // 
	        counterRegion.tell(new GET(123), null);
	        
	        // 
	        counterRegion.tell(new Counter.EntityEnvelope(124,Counter.CounterOp.INCREMENT), null);

	        /*
	         *   SupervisorCounter for supervisorStrategy.
	         */
	        
	        ClusterSharding.get(system).start("SupervisedCounter",Props.create(CounterSupervisor.class), settings, messageExtractor);

	}

	public static void main(String[] args) {
		ClusterShardingApp app = new ClusterShardingApp();
		app.demonstrateUsage();
	}
}
