package akka.future.example;

import java.time.Duration;

import akka.actor.ActorSystem;
import akka.agent.Agent;
import akka.dispatch.ExecutionContexts;
import akka.dispatch.Mapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.Await;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

/**
 * Hello world!
 *
 */
public class AgentExample 
{
	ActorSystem system = ActorSystem.create("akka");

	private LoggingAdapter log = Logging.getLogger(system, this);

	
	public void createAgent(){
		ExecutionContext ex =ExecutionContexts.global();
		Agent<Integer> agent = Agent.create(10, ex);
		log.debug("value before updating = "+agent.get());
		agent.send(7);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			log.error("Interrupted "+e.getMessage());
		}
	
		log.debug("value after updating = "+agent.get());
			

		agent.send(new Mapper<Integer, Integer>() {
			  public Integer apply(Integer i) {
			    return i * 2;			    
			  }
			});
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			log.error("Interrupted "+e.getMessage());
		}
		log.debug("value before updating with function = "+agent.get());
		system.terminate();
	}
    public static void main( String[] args )
    {
    	AgentExample example = new AgentExample();
    	example.createAgent();
    }
}
