package akka.stream.test.example;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Pair;
import akka.stream.*;
import akka.stream.javadsl.*;
import akka.stream.testkit.TestPublisher;
import akka.stream.testkit.TestSubscriber;
import akka.stream.testkit.javadsl.TestSink;
import akka.stream.testkit.javadsl.TestSource;
import akka.testkit.JavaTestKit;
import scala.concurrent.duration.FiniteDuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class RecipeManualTrigger {

	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	class Trigger {
	}

	public final Trigger TRIGGER = new Trigger();

	public void zipped() throws Exception {
	    new JavaTestKit(system) {
	      {
	        final Source<Trigger, TestPublisher.Probe<Trigger>> triggerSource = TestSource.probe(system);
	        final Sink<Message, TestSubscriber.Probe<Message>> messageSink = TestSink.probe(system);

	        //#manually-triggered-stream
	        final RunnableGraph<Pair<TestPublisher.Probe<Trigger>, TestSubscriber.Probe<Message>>> g =
	          RunnableGraph.<Pair<TestPublisher.Probe<Trigger>, TestSubscriber.Probe<Message>>>fromGraph(
	            GraphDSL.create(
	              triggerSource,
	              messageSink,
	              (p, s) -> new Pair<>(p, s),
	              (builder, source, sink) -> {
	                SourceShape<Message> elements =
	                  builder.add(Source.from(Arrays.asList("1", "2", "3", "4")).map(t -> new Message(t)));
	                FlowShape<Pair<Message, Trigger>, Message> takeMessage =
	                  builder.add(Flow.<Pair<Message, Trigger>>create().map(p -> p.first()));
	                final FanInShape2<Message, Trigger, Pair<Message, Trigger>> zip =
	                  builder.add(Zip.create());
	                builder.from(elements).toInlet(zip.in0());
	                builder.from(source).toInlet(zip.in1());
	                builder.from(zip.out()).via(takeMessage).to(sink);
	                return ClosedShape.getInstance();
	              }
	            )
	          );
	        //#manually-triggered-stream

	        Pair<TestPublisher.Probe<Trigger>, TestSubscriber.Probe<Message>> pubSub = g.run(materializer);
	        TestPublisher.Probe<Trigger> pub = pubSub.first();
	        TestSubscriber.Probe<Message> sub = pubSub.second();

	        FiniteDuration timeout = FiniteDuration.create(100, TimeUnit.MILLISECONDS);
	        sub.expectSubscription().request(1000);
	        sub.expectNoMsg(timeout);

	        pub.sendNext(TRIGGER);
	        sub.expectNext(new Message("1"));
	        sub.expectNoMsg(timeout);

	        pub.sendNext(TRIGGER);
	        pub.sendNext(TRIGGER);
	        sub.expectNext(new Message("2"));
	        sub.expectNext(new Message("3"));
	        sub.expectNoMsg(timeout);

	        pub.sendNext(TRIGGER);
	        sub.expectNext(new Message("4"));
	        sub.expectComplete();
	      }
	    };
	  }

	public static void main(String[] args) throws Exception {
		RecipeManualTrigger app =new RecipeManualTrigger();
		app.zipped();
	}
}

class Message {
	final String msg;

	public Message(String msg) {
		super();
		this.msg = msg;
	}

	@Override
	public boolean equals(Object obj) {
		Message message = (Message)obj;
		return message instanceof Message;
	}
}
