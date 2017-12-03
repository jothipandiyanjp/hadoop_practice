package akka.stream.example;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import akka.stream.example.IntegrationExample.Save;

public class DatabaseService extends AbstractActor {
	public final ActorRef probe;

	DatabaseService(ActorRef probe) {
		this.probe = probe;

		receive(ReceiveBuilder.match(Save.class, s -> {
			probe.tell(s.tweet.author.handle, ActorRef.noSender());
			sender().tell(SaveDone.INSTANCE, self());
		}).build());
	}

	static class SaveDone {
		public static SaveDone INSTANCE = new SaveDone();

		private SaveDone() {
		}
	}
}
