package com.akka.actors;

import com.akka.actors.helper.WorkerApi.Progress;

import scala.concurrent.duration.Duration;
import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Listener extends UntypedActor{

	
    final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void preStart() throws Exception {
        getContext().setReceiveTimeout(Duration.create("15 seconds"));

    }
	@Override
	public void onReceive(Object msg) throws Exception {
		log.debug("recevied message ",msg);
		
		if(msg instanceof Progress){
	        Progress progress = (Progress) msg;
	        log.info("Current progress: {} %", progress.percent);
	        if (progress.percent >= 100.0) {
	            log.info("That's all, shutting down");
	            getContext().system().terminate();
	        }
	      } else if (msg == ReceiveTimeout.getInstance()) {
	          log.error("Shutting down due to unavailable service");
	          getContext().system().terminate();
	      }else
	    	  unhandled(msg);
	}
}
