package com.bizruntime.akka;

import akka.actor.UntypedActor;

public class Greeter extends UntypedActor{

	
	@Override
	public void onReceive(Object msg) throws Exception {
		
		if(msg == Msg.GREET){
			getSender().tell(Msg.DONE, getSelf());
		}else
			unhandled(msg);
	}
	
	public static enum Msg{
		GREET, DONE;
	}
	
	
}
