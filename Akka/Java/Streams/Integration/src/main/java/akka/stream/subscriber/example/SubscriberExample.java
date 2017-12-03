package akka.stream.subscriber.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import akka.stream.actor.AbstractActorSubscriber;
import akka.stream.actor.ActorSubscriberMessage;
import akka.stream.actor.MaxInFlightRequestStrategy;
import akka.stream.actor.RequestStrategy;


public class SubscriberExample {

	public static class WorkerPoolProtocol {

		public static class Msg {
			public final int id;
			public final ActorRef replyTo;

			public Msg(int id, ActorRef replyTo) {
				this.id = id;
				this.replyTo = replyTo;
			}

			@Override
			public String toString() {
				return String.format("Msg(%s, %s)", id, replyTo);
			}
		}

		public static Msg msg(int id, ActorRef replyTo) {
			return new Msg(id, replyTo);
		}

		public static class Work {
			public final int id;

			public Work(int id) {
				this.id = id;
			}

			@Override
			public String toString() {
				return String.format("Work(%s)", id);
			}
		}

		public static Work work(int id) {
			return new Work(id);
		}

		public static class Reply {
			public final int id;

			public Reply(int id) {
				this.id = id;
			}

			@Override
			public String toString() {
				return String.format("Reply(%s)", id);
			}
		}

		public static Reply reply(int id) {
			return new Reply(id);
		}

		public static class Done {
			public final int id;

			public Done(int id) {
				this.id = id;
			}

			@Override
			public String toString() {
				return String.format("Done(%s)", id);
			}

			@Override
			public boolean equals(Object o) {
				if (this == o) {
					return true;
				}
				if (o == null || getClass() != o.getClass()) {
					return false;
				}

				Done done = (Done) o;

				if (id != done.id) {
					return false;
				}

				return true;
			}

			@Override
			public int hashCode() {
				return id;
			}
		}

		public static Done done(int id) {
			return new Done(id);
		}

	}

	public static class WorkerPool extends AbstractActorSubscriber {

		public static Props props() {
			return Props.create(WorkerPool.class);
		}

		final int MAX_QUEUE_SIZE = 10;
		final Map<Integer, ActorRef> queue = new HashMap<>();
		  final Router router;

		@Override
		public RequestStrategy requestStrategy() {
			return new MaxInFlightRequestStrategy(MAX_QUEUE_SIZE) {

				@Override
				public int inFlightInternally() {
					return queue.size();
				}
			};
		}

		public WorkerPool() {
		    final List<Routee> routees = new ArrayList<>();
		    
		    for (int i = 0; i < 3; i++)
		        routees.add(new ActorRefRoutee(context().actorOf(Props.create(Worker.class))));
		    
		    router = new Router(new RoundRobinRoutingLogic(), routees);

		    receive(ReceiveBuilder.
		    	    match(ActorSubscriberMessage.OnNext.class, on -> on.element() instanceof WorkerPoolProtocol.Msg,
		    	      onNext -> {
		    	        WorkerPoolProtocol.Msg msg = (WorkerPoolProtocol.Msg) onNext.element();
		    	        queue.put(msg.id, msg.replyTo);
		    	 
		    	        if (queue.size() > MAX_QUEUE_SIZE)
		    	          throw new RuntimeException("queued too many: " + queue.size());
		    	              
		    	        router.route(WorkerPoolProtocol.work(msg.id), self());
		    	    }).
		    	    match(WorkerPoolProtocol.Reply.class, reply -> {
		    	      int id = reply.id;
		    	      queue.get(id).tell(WorkerPoolProtocol.done(id), self());
		    	      queue.remove(id);
		    	    }).
		    	    build());
		}

	}
	static class Worker extends AbstractActor {
		  public Worker() {
			    receive(ReceiveBuilder.
			      match(WorkerPoolProtocol.Work.class, work -> {
			        sender().tell(WorkerPoolProtocol.reply(work.id), self());
			      }).build());
			  }
	}

}
