package com.akka.actors;

import static akka.actor.SupervisorStrategy.escalate;

import static akka.actor.SupervisorStrategy.stop;

import com.akka.actors.helper.CounterServiceApi;
import com.akka.actors.helper.CounterServiceApi.CurrentCount;
import com.akka.actors.helper.Increment;
import com.akka.actors.helper.WorkerApi;
import com.akka.actors.helper.WorkerApi.Progress;
import com.akka.custom.exception.ServiceUnavailable;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.actor.SupervisorStrategy.Directive;
import akka.dispatch.Mapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import static akka.japi.Util.classTag;
import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;
import akka.util.Timeout;

public class Worker extends UntypedActor {
	final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    final Timeout askTimeout = new Timeout(Duration.create(5, "seconds"));
    final int totalCount = 51;

		
    ActorRef progressListener;

	private ActorRef counterService = getContext().actorOf(Props.create(CounterService.class),"counter");

	
	private static SupervisorStrategy strategy = new OneForOneStrategy(-1,
			Duration.Inf(), new Function<Throwable, Directive>() {

				public Directive apply(Throwable t) {
					if (t instanceof ServiceUnavailable)
						return stop();
					else
						return escalate();
				}
			});

	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	};

	@Override
	public void onReceive(Object msg) throws Exception {
		log.debug("recevied message : "+msg);
		if(msg.equals(WorkerApi.Start) && progressListener == null){
			progressListener = getSender();
			
			getContext().system()
						.scheduler()
						.schedule(Duration.Zero(), 
									Duration.create(1,"seconds"), 
									getSelf(), 
									WorkerApi.Do, getContext().dispatcher(), 
									null);

		}else if(msg.equals(WorkerApi.Do)){
			counterService.tell(new Increment(1), getSelf());
			counterService.tell(new Increment(1), getSelf());
			counterService.tell(new Increment(1), getSelf());
			
			pipe(ask(counterService,CounterServiceApi.GetCurrentCount,askTimeout)
					.mapTo(classTag(CurrentCount.class))
					.map(new Mapper<CurrentCount, Progress>() {
						@Override
						public Progress apply(CurrentCount c) {
							return new Progress(100.0 * c.count / totalCount);
						}
					}, getContext().dispatcher()),getContext().dispatcher())
					.to(progressListener);
					
		}else{
			unhandled(msg);
		}
			
	}
}
