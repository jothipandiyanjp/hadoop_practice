package com.bizruntime.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.akka.actors.Listener;
import com.akka.actors.Worker;
import static com.akka.actors.helper.WorkerApi.Start;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


public class FaultHandlingSample {

	public static void main(String[] args) {
		FaultHandlingSample sample =new FaultHandlingSample();
		sample.FaultHandlingExample();
		
	}
	
	void FaultHandlingExample(){
		Config config = ConfigFactory.parseString("akka.loglevel = DEBUG \n" +
			      		"akka.actor.debug.lifecycle = on");
		
		ActorSystem system =  ActorSystem.create("FaultHandlingSample", config);
		  ActorRef worker = system.actorOf(Props.create(Worker.class), "worker");
		    ActorRef listener = system.actorOf(Props.create(Listener.class), "listener");
		    worker.tell(Start, listener);

	}
}
