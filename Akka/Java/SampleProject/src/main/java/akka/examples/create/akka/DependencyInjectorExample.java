package akka.examples.create.akka;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.IndirectActorProducer;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class DependencyInjectorExample  extends UntypedActor {

	@Override
		public void onReceive(Object arg0) throws Exception {
			
		}
	/*	final ActorRef myActor = getContext().actorOf(
			    Props.create(DependencyInjectorExample.class, applicationContext, "MyActor"),
			      "myactor3");	
	 */
class DependencyInjector implements IndirectActorProducer{
	final Object applicationContext;
	final String beanName;
	
	public DependencyInjector(Object applicationContext, String beanName) {
		this.applicationContext = applicationContext;
		this.beanName = beanName;
	}
	
	public Actor produce() {
		MyActor actor = null;
		return actor;
	}
	
	public Class<? extends Actor> actorClass() {
		
		return MyActor.class;
	}
	
}

}
