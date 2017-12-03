package akka.stream.subscriber.example;

import java.util.ArrayList;

import java.util.List;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.subscriber.example.SubscriberExample.WorkerPool;
import akka.stream.subscriber.example.SubscriberExample.WorkerPoolProtocol;

public class SubscriberExampleApp {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer mat = ActorMaterializer.create(system);

	public static void main(String[] args) {
		SubscriberExampleApp app = new SubscriberExampleApp();
		app.subscriber();
	}

	public void subscriber() {
		final int N = 127;
		final List<Integer> data = new ArrayList<>(N);
		for (int i = 0; i < N; i++) 
		  data.add(i);
		
		 
		Source.from(data)
		  .map(i -> WorkerPoolProtocol.msg(i, system.actorOf(Props.create(Reply.class))))
		  .runWith(Sink.<WorkerPoolProtocol.Msg>actorSubscriber(WorkerPool.props()), mat);

	}


		
}
