

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Pair;
import akka.japi.pf.ReceiveBuilder;
import akka.stream.ActorMaterializer;
import akka.stream.ClosedShape;
import akka.stream.FanInShape2;
import akka.stream.FlowShape;
import akka.stream.Materializer;
import akka.stream.SourceShape;
import akka.stream.actor.AbstractActorPublisher;
import akka.stream.actor.AbstractActorSubscriber;
import akka.stream.actor.MaxInFlightRequestStrategy;
import akka.stream.actor.RequestStrategy;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Zip;

public class ProgramatticTriggerApp {
	
	private final ActorSystem system = ActorSystem.create();
	private final Materializer materializer = ActorMaterializer.create(system);

	static class Worker1 extends AbstractActorPublisher<Trigger> {

		public static Props props() {
			return Props.create(Worker1.class);
		}

		public Worker1() {
			receive(ReceiveBuilder
					.match(Trigger.class,
							i -> System.out.println("JobManager -> " + i))
					.matchAny(i -> System.out.println("JobManager -> " + i))
					.build());
		}
	}

	static class Worker2 extends AbstractActorSubscriber {
		final int MAX_QUEUE_SIZE = 1000;
		final Map<Integer, ActorRef> queue = new HashMap<>();

		public Worker2() {
			receive(ReceiveBuilder.matchAny(
					i -> System.out.println("Worker -> " + i)).build());
		}

		@Override
		public RequestStrategy requestStrategy() {
			return new MaxInFlightRequestStrategy(MAX_QUEUE_SIZE) {

				@Override
				public int inFlightInternally() {
					return queue.size();
				}
			};
		}

		public static Props props() {
			return Props.create(Worker2.class);
		}

	}

	public final Trigger TRIGGER = new Trigger();

	public void zip() {

		final Source<Trigger, ActorRef> jobManagerSource = Source
				.actorPublisher(Worker1.props());
		final Sink<Message, ActorRef> jobManagerSink = Sink
				.actorSubscriber(Worker2.props());

		RunnableGraph<Pair<ActorRef, ActorRef>> graph = RunnableGraph
				.fromGraph(GraphDSL.create(
						jobManagerSource,
						jobManagerSink,
						(p, s) -> new Pair<>(p, s),
						(builder, source, sink) -> {

							SourceShape<Message> elements = builder.add(Source
									.from(Arrays.asList("1", "2", "3", "4"))
									.map(t -> new Message(t)));

							FlowShape<Pair<Message, Trigger>, Message> takeMessage = builder
									.add(Flow.<Pair<Message, Trigger>> create()
											.map(p -> p.first()));

							final FanInShape2<Message, Trigger, Pair<Message, Trigger>> zip = builder
									.add(Zip.create());

							builder.from(elements).toInlet(zip.in0());
							builder.from(source).toInlet(zip.in1());
							builder.from(zip.out()).via(takeMessage).to(sink);

							return ClosedShape.getInstance();
						})
				);
		Pair<ActorRef, ActorRef> pair = graph.run(materializer);
		ActorRef pub = pair.first();
		pub.tell(TRIGGER, ActorRef.noSender());
		pub.tell(TRIGGER, ActorRef.noSender());
		
	}

	public static void main(String[] args) {
		ProgramatticTriggerApp app = new ProgramatticTriggerApp();
		app.zip();
	}
}

class Trigger {
	
}

class Message {
	final String msg;

	public Message(String msg) {
		super();
		this.msg = msg;
	}

	
}
