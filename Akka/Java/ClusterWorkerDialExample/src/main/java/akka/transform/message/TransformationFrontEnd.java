package akka.transform.message;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.transform.message.TransformationMessages.TransformationJob;
import akka.transform.message.TransformationMessages.JobFailed;

public class TransformationFrontEnd extends UntypedActor{
	
	  List<ActorRef> backends = new ArrayList<ActorRef>();
	  int jobCounter=0;

	  @Override
	public void onReceive(Object message) throws Exception {
		 if(message instanceof TransformationJob && backends.isEmpty()){
		      TransformationJob job = (TransformationJob) message;
		      getSender().tell(new JobFailed("Service unavailable, try again later..", job), getSelf());
		 }else if(message instanceof TransformationJob){
		      TransformationJob job = (TransformationJob) message;
		      jobCounter++;
		      backends.get(jobCounter % backends.size()).forward(job, getContext());
		}else if(message.equals(TransformationMessages.BACKEND_REGISTRATION)){
			getContext().watch(getSender());
			backends.add(getSender());
		}else if(message instanceof Terminated ){
			Terminated terminated = (Terminated)message;
			backends.remove(terminated.getActor());
		}else
			unhandled(message);
	}
}
