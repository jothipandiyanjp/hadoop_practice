package akka.future.example;



import akka.actor.ActorSystem;
import akka.agent.Agent;
import akka.dispatch.ExecutionContexts;
import akka.dispatch.Mapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.Await;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

/**
 * Hello world!
 *
 */
public class Agent_SendOff 
{
	ActorSystem system = ActorSystem.create("akka");

	private LoggingAdapter log = Logging.getLogger(system, this);

	
	public void createAgent(){
		ExecutionContext ex =ExecutionContexts.global();
		Agent<Integer> agent = Agent.create(10, ex);
		log.debug("value before updating = "+agent.get());
		agent.send(7);
		Integer i;
		try {
			i = Await.result(agent.future(),Duration.create(5,"s"));
			log.debug("value after updating = "+agent.get());
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	
		agent.sendOff(new Mapper<Integer, Integer>() {
			@Override
			public Integer apply(Integer i) {
				
				return i * i;
			}
		},ex);
		
		  try {
			  i = Await.result(agent.future(),Duration.create(5,"s"));
				log.debug("value after updating = "+agent.get());
		} catch (Exception e1) {
		}

	
		
		system.terminate();
	}
    public static void main( String[] args )
    {
    	Agent_SendOff example = new Agent_SendOff();
    	example.createAgent();
    }
}
