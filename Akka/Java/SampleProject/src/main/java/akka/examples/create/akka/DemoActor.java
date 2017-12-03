package akka.examples.create.akka;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class DemoActor  extends UntypedActor{
	  final int magicNumber;

	
	public DemoActor(int magicNUmber) {
		this.magicNumber = magicNUmber;
	}


	public static Props props(final int magicNUmber){	
		
		return Props.create(DemoActor.class,new String("a"));
	
	}
	
	
	@Override
	public void onReceive(Object msg) throws Exception {
		
	}
	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("MySystem");
		  system.actorOf(DemoActor.props(42), "demo");
	}
	
}
