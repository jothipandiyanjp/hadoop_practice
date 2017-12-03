package akka.stream.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.function.Function;
import akka.japi.function.Function2;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Attributes;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Framing;
import akka.stream.javadsl.FramingTruncation;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.SubSource;
import akka.util.ByteString;

public class GroupByApp {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	static class Topic {
		private final String name;

		public Topic(String name) {
			this.name = name;
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Topic topic = (Topic) o;
			if (name != null ? !name.equals(topic.name) : topic.name != null) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			return name != null ? name.hashCode() : 0;
		}

	}

	class Message {
		final String msg;

		public Message(String msg) {
			super();
			this.msg = msg;
		}

	}

	final List<Topic> extractTopics(Message m) {
		final List<Topic> topics = new ArrayList<>(2);

		if (m.msg.startsWith("1")) {
			topics.add(new Topic("1"));
		} else {
			topics.add(new Topic("1"));
			topics.add(new Topic("2"));
		}
		return topics;

	}

	public void digestExample() {
		final int onElement = Logging.DebugLevel();
		final int onFinish = Logging.InfoLevel();
		final int onFailure = Logging.ErrorLevel();
		final Source<Message, NotUsed> elems = Source.from(
				Arrays.asList("1: a", "1: b", "all: c", "all: d", "1: e")).map(
				s -> new Message(s));

		final Function<Message, List<Topic>> topicMapper = m -> extractTopics(m);

		final Source<Pair<Message, Topic>, NotUsed> messageAndTopic = elems
				.mapConcat((Message msg) -> {
					List<Topic> topicsForMessage = topicMapper.apply(msg);
					return topicsForMessage.stream()
							.map(topic -> new Pair<Message, Topic>(msg, topic))
							.collect(Collectors.toList());
				});

		SubSource<Pair<Message, Topic>, NotUsed> multiGroups = messageAndTopic
				.withAttributes(
						Attributes.createLogLevels(onElement, onFinish,
								onFailure))
				.groupBy(2, pair -> pair.second())
				.map(pair -> {
					Message message = pair.first();
					Topic topic = pair.second();
					System.out.println(message.msg);
					return pair;
				});

		CompletionStage<List<String>> result = multiGroups.map(i -> {System.out.println("->"+i.first().msg);return i;})
				.grouped(5).map(i -> {System.out.println("-->"+i);return i;})
				.mergeSubstreams().map(i -> {System.out.println("--->"+i);return i;})
				.<String> map(
						pair -> {
							Topic topic = pair.get(0).second();
							return topic.name
									+ mkString(
											pair.stream()
													.map(p -> p.first().msg)
													.collect(
															Collectors.toList()),
											"[", ", ", "]");
						}).grouped(10).runWith(Sink.head(), materializer);

		List<String> got = null;
		try {
			got = result.toCompletableFuture().get(3, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}

		log.debug("" + got);
	}

	public static final String mkString(List<String> l, String start,
			String separate, String end) {
		System.out.println(l);
		StringBuilder sb = new StringBuilder(start);
		for (String s : l) {
			sb.append(s).append(separate);
		}
		return sb.delete(sb.length() - separate.length(), sb.length())
				.append(end).toString();
	}

	public static void main(String[] args) {
		GroupByApp example = new GroupByApp();
		example.digestExample();
	}
}
