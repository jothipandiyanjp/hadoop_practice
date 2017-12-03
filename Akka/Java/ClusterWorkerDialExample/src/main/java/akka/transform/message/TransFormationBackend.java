package akka.transform.message;

import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.transform.message.TransformationMessages.TransformationJob;

public class TransFormationBackend extends UntypedActor{
	LoggingAdapter log =Logging.getLogger(getContext().system(), this);
	
	Cluster cluster = Cluster.get(getContext().system());
	
	@Override
	public void preStart() throws Exception {
		cluster.subscribe(getSelf(),MemberUp.class);
		
	}
	
	@Override
	public void postStop() throws Exception {
			cluster.unsubscribe(getSelf());
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		log.info("Onreceive -> "+message);
		if(message instanceof TransformationJob){
		      TransformationJob job = (TransformationJob) message;
		      getSender().tell(new TransformationJob(job.getText().toUpperCase()), getSelf());
		}else if(message instanceof CurrentClusterState){
		      CurrentClusterState state = (CurrentClusterState) message;
		      	for(Member member : state.getMembers()){
		      		if(member.status().equals(MemberStatus.up())){
		      			log.info("Member state --> "+ member+" going to register ");
		      			register(member);
		      			
		      		}
		      	}
		}else if(message instanceof MemberUp){
		      MemberUp mUp = (MemberUp) message;
		      register(mUp.member());
		}else{
			unhandled(message);
		}
	}
	
	void register(Member member){
		if(member.hasRole("frontend")){
			getContext().actorSelection(member.address()+"/user/frontend")
						.tell(TransformationMessages.BACKEND_REGISTRATION,getSelf());
		}
	}
}
