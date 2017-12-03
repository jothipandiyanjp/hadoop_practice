package akka.monitoring.actor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

public class PosionPillExample {

	public static class WatchActor extends UntypedActor{
			
		
		ActorRef lastSender = getContext().system().deadLetters();
		@Override
		public void onReceive(Object message) throws Exception {
			
			if(message.equals("kill")){
				
				lastSender = getSender();
				
			}else unhandled(message);
		
			
		}
	}
	
	public void doSomeThing(){
		
		ActorSystem system = ActorSystem.create("akka");
		Inbox inbox = Inbox.create(system);
		
		ActorRef ref = system.actorOf(Props.create(WatchActor.class),"parent");
		inbox.send(ref,"kil");	

		inbox.send(ref, PoisonPill.getInstance());	

			try {
			inbox.receive(Duration.create(5, TimeUnit.SECONDS));
			
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
	}
	
	
	public static void main(String[] args) {
		PosionPillExample example = new PosionPillExample();
		example.doSomeThing();
	}
}
