package akka.cluster.example;

import akka.actor.UntypedActor;

public class SampleActor extends UntypedActor{
	
	@Override
	public void onReceive(Object message) throws Exception {
		Thread.sleep(1000);
		System.out.println("Message received "+message);

	}
}
