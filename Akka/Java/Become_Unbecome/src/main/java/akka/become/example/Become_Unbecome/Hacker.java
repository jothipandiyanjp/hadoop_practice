package akka.become.example.Become_Unbecome;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import static akka.become.example.Become_Unbecome.Messages.*;
import static java.util.concurrent.TimeUnit.*;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;

public class Hacker extends AbstractActor {
	  private final LoggingAdapter log = Logging.getLogger(context().system(), this);
	private String name;
	private ActorRef left;
	private ActorRef right;
	public Hacker(String name, ActorRef left, ActorRef right) {
		super();
		this.name = name;
		this.left = left;
		this.right = right;
	      receive(ReceiveBuilder.matchEquals(Think, m -> {
	          log.info(String.format("%s starts to think", name));
	          startThinking(Duration.create(5, SECONDS));
	        }).build());

	}

	PartialFunction<Object, BoxedUnit> eating = ReceiveBuilder.
		      matchEquals(Think, m -> {
		        left.tell(new Put(self()), self());
		        right.tell(new Put(self()), self());
		        log.info(String.format("%s puts down his chopsticks and starts to think", name));
		        startThinking(Duration.create(5, SECONDS));
		      }).build();

	
	PartialFunction<Object, BoxedUnit> waitingFor(ActorRef chopstickToWaitFor, ActorRef otherChopstick) {
	      return ReceiveBuilder.
	        match(Taken.class, t -> t.chopstick == chopstickToWaitFor, t -> {
	          log.info(String.format("%s has picked up %s and %s and starts to eat",
	            name, left.path().name(), right.path().name()));
	          context().become(eating);
	          context().system().scheduler().scheduleOnce(Duration.create(5, SECONDS), self(), Think, context().system().dispatcher(), self());
	        }).
	        match(Busy.class, b -> {
	          otherChopstick.tell(new Put(self()), self());
	          startThinking(Duration.create(10, MILLISECONDS));
	        }).
	        build();
	    }
	PartialFunction<Object, BoxedUnit> deniedAChopstick = ReceiveBuilder.
		      match(Taken.class, t -> {
		        t.chopstick.tell(new Put(self()), self());
		        startThinking(Duration.create(10, MILLISECONDS));
		      }).
		      match(Busy.class, b ->{
		    	  startThinking(Duration.create(10, MILLISECONDS));}).
		      build();

	PartialFunction<Object, BoxedUnit> hungry = ReceiveBuilder.
		      match(Taken.class, t -> t.chopstick == left,
		        t -> context().become(waitingFor(right, left))).
		      match(Taken.class, t -> t.chopstick == right,
		        t -> context().become(waitingFor(left, right))).
		      match(Busy.class,
		        b -> context().become(deniedAChopstick)).
		      build();

	PartialFunction<Object, BoxedUnit> thinking = ReceiveBuilder.
    	      matchEquals(Eat, m -> {
    	        context().become(hungry);
    	        left.tell(new Take(self()), self());
    	        right.tell(new Take(self()), self());
    	      }).build();

	private void startThinking(FiniteDuration duration) {
	      context().become(thinking);
	      context().system().scheduler().scheduleOnce(duration, self(), Eat, context().system().dispatcher(), self());
	      
	}

	
}
