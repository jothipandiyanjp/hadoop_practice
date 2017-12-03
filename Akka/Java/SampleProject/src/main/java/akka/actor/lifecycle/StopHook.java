package akka.actor.lifecycle;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

public class StopHook {

	public static class WatchActor extends UntypedActor{
			
		
		ActorRef lastSender = getContext().system().deadLetters();
	
		
		@Override
		public void onReceive(Object message) throws Exception {
			
			if(message.equals("kill")){
				getContext().stop(getSelf());				
				lastSender = getSender();
			}else unhandled(message);
		}
		
		@Override
		public void postStop() throws Exception {
			lastSender.tell("postStop c", getSelf());
			super.postStop();
		}
	}
	
	public void doSomeThing(){
		
		ActorSystem system = ActorSystem.create("akka");
		Inbox inbox = Inbox.create(system);
		
		ActorRef ref = system.actorOf(Props.create(WatchActor.class),"parent");

			try {
			inbox.send(ref, "kill");	

			inbox.receive(Duration.create(5, TimeUnit.SECONDS));

			inbox.send(ref, "kill");	

			} catch (TimeoutException e) {
				e.printStackTrace();
			}
	}
	
	
	public static void main(String[] args) {
		StopHook example = new StopHook();
		example.doSomeThing();
	}
}
