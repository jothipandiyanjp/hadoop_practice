package akka.circuit.breaker.example;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Scheduler;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.CircuitBreaker;
import akka.pattern.Patterns;
import akka.util.Timeout;

public class SimpleCircuitBreaker extends UntypedActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(),
			this);

	private final CircuitBreaker breaker;

	public static final int MAX_FAILURES = 2;
	public static final Timeout ASK_TIMEOUT = Timeout.apply(100,TimeUnit.MILLISECONDS);
	public static final FiniteDuration CALL_TIMEOUT = Duration.create(100,TimeUnit.MILLISECONDS);
	public static final FiniteDuration RESET_TIMEOUT = Duration.create(2,TimeUnit.SECONDS);

	  private final ActorRef service;


	public static Props props(Props serviceProps) {
	
		return Props.create(SimpleCircuitBreaker.class,serviceProps);
	}
	
	public SimpleCircuitBreaker(Props serviceProps) {
		this.service = getContext().actorOf(serviceProps,"Service");

		ExecutionContextExecutor executor = getContext().dispatcher();
		Scheduler scheduler = getContext().system().scheduler();

		this.breaker = new CircuitBreaker(executor, scheduler, MAX_FAILURES,
				CALL_TIMEOUT, RESET_TIMEOUT)
		.onOpen(new Runnable() {
			public void run() {
				log.info("Circuit Breaker is open");
			}
		}).onClose(new Runnable() {
			public void run() {
				log.info("Circuit Breaker is closed");
			}
		}).onHalfOpen(new Runnable() {

			public void run() {
				log.info("Circuit Breaker is half open, next message will go through");
			}
		});

	}

	
	@Override
	public void onReceive(Object message) throws Exception {
		   if ( message instanceof Service.Task ) {
			     final Service.Task task = (Service.Task) message;
			     
			    // Without circuit breaker
			  
/*			    
			       Future cbFuture = Futures.future(new Callable<Future<Object>>() {
			    	public Future<Object> call() throws Exception {
			    		return Patterns.ask( service, task, ASK_TIMEOUT );
			    	}
			      },getContext().dispatcher());
			 
*/			     

			 // With CircuitBreaker
			      Future<Object> cbFuture = breaker.callWithCircuitBreaker(new Callable<Future<Object>>() {
			    	  public Future<Object> call() throws Exception {
			    		return Patterns.ask( service, task, ASK_TIMEOUT );
			    	}
			      });
			      
		// sending future result to actor      
		Patterns.pipe( cbFuture, getContext().system().dispatcher() ).to( getSender() );
		   }
	}

}
