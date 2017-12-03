package akka.logging.example;

import scala.concurrent.duration.Deadline;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class MyActorApp {

	public void logExample() {

		ActorSystem system = ActorSystem.create("AkkaSystem");
		ActorRef actor = system.actorOf(Props.create(MyActor.class), "MyActor");
		actor.tell("test", ActorRef.noSender());
		actor.tell("MyApp", ActorRef.noSender());

	}

	public static void main(String[] args) {
		MyActorApp app = new MyActorApp();
		app.logExample();
	}

}
