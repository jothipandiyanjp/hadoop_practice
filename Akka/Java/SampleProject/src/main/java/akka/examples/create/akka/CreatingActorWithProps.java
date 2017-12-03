package akka.examples.create.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorContext;

public class CreatingActorWithProps {
	public static void main(String[] args) {
		CreatingActorWithProps example = new CreatingActorWithProps();
		example.doSomeOperations();
	}
	
	void doSomeOperations(){
		
		final ActorSystem system = ActorSystem.create("MySystem"); 	//Heavy Object Create Only one
		ActorRef actor=system.actorOf(Props.create(MyUnTypedActor.class), "myActor");
		ActorRef actor1=system.actorOf(Props.create(A.class), "myActor1");
		
		
	}
}

class A extends UntypedActor{

	final  ActorRef child=getContext().actorOf(Props.create(MyUnTypedActor.class),"mychild");
	
	@Override
	public void onReceive(Object arg0) throws Exception {
	}
}