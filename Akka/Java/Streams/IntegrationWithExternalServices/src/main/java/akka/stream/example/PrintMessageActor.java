package akka.stream.example;

import akka.actor.UntypedActor;

public class PrintMessageActor extends UntypedActor{
	
	@Override
	public void onReceive(Object msg) throws Exception {
		System.out.println("msg -> "+msg);
	}
}
