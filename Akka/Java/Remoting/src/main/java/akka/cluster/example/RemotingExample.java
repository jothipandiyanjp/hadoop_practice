package akka.cluster.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Deploy;
import akka.actor.Props;
import akka.remote.RemoteScope;

public class RemotingExample {
	public static void main(String[] args) {

		ActorSystem system = ActorSystem.create("ClusterSystem");
		Address addr = new Address("akka.tcp", "ClusterSystem", "192.168.1.193", 3560);
		
		ActorRef ref = system.actorOf(Props.create(SampleActor.class).withDeploy( new Deploy(new RemoteScope(addr))),"sample");
		ref.tell("Pretty slick", ActorRef.noSender());

	}
}
