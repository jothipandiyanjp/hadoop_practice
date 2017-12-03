package akka.cluster.example;

import akka.actor.ActorInitializationException;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.japi.pf.DeciderBuilder;

public class CounterSupervisor extends UntypedActor{

    private final ActorRef counter = getContext().actorOf(Props.create(Counter.class), "theCounter");
    
    private static final SupervisorStrategy strategy = new OneForOneStrategy(DeciderBuilder.
            match(IllegalArgumentException.class, e -> SupervisorStrategy.resume()).
            match(ActorInitializationException.class, e -> SupervisorStrategy.stop()).
            match(Exception.class, e -> SupervisorStrategy.restart()).
            matchAny(o -> SupervisorStrategy.escalate()).build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
    	return strategy;
    }
    
    @Override
    public void onReceive(Object message) throws Exception {
    	counter.forward(message, getContext());
    }
    
}
