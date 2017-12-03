package akka.actor.lifecycle;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import scala.Option;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class PreRestart {
	ActorSystem system = ActorSystem.create("akka");
	private LoggingAdapter log = Logging.getLogger(system, this);
	static int i=0;
	public static class WatchActor extends UntypedActor{
		{
			log.debug("instance"+getSelf()+this.hashCode());
		}
			ActorRef child ;
		
		ActorRef lastSender  ;
		
		@Override
		public void preStart() throws Exception {		
			
			super.preStart();
			lastSender = getContext().system().deadLetters();
			child = this.getContext().actorOf(Props.empty(),"child");	
			this.getContext().watch(child);			
		}
		

		@Override
		public void preRestart(Throwable reason, Option<Object> message)
				throws Exception {
			lastSender.tell("going to restart", getSelf());
			super.preRestart(reason, message);
		}
		
		@Override
		public void onReceive(Object message) throws Exception {
			if(message.equals("kill")){
				lastSender = getSender();
				if(i==0){
					lastSender.tell("exception going to occur", getSelf());
					throw new IOException();
				}
				lastSender.tell("ok fine", getSelf());
					
			}else unhandled(message);
		}
		
		@Override
		public void postStop() throws Exception {
			lastSender.tell("Actor Stopped", getSelf());
			i=2;
			super.postStop();
		}
	}
	
	public void doSomeThing(){
		

		Inbox inbox = Inbox.create(system);
		
		ActorRef ref = system.actorOf(Props.create(WatchActor.class),"parent");

			try {
			inbox.send(ref, "kill");	

			inbox.receive(Duration.create(5, TimeUnit.SECONDS));

			inbox.send(ref, "kill");	
			inbox.receive(Duration.create(5, TimeUnit.SECONDS));
			
			inbox.send(ref, "kill");	
			inbox.receive(Duration.create(5, TimeUnit.SECONDS));
			inbox.receive(Duration.create(5, TimeUnit.SECONDS));
			inbox.receive(Duration.create(5, TimeUnit.SECONDS));

			} catch (TimeoutException e) {
				e.printStackTrace();
			}
			
	}
	
	
	public static void main(String[] args) {
		PreRestart example = new PreRestart();
		example.doSomeThing();
	}
}
