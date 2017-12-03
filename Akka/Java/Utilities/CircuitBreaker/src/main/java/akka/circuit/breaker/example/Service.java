package akka.circuit.breaker.example;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

public class Service extends UntypedActor {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public static Props props() {
		return Props.create(Service.class);
	}

	public Service() {
		FiniteDuration duration = Duration.create(2, TimeUnit.SECONDS);
		getContext()
				.system()
				.scheduler()
				.schedule(duration, duration, getSelf(), new Swap(),
						getContext().dispatcher(), getSelf());
	}

	Procedure<Object> slow = new Procedure<Object>() {
		public void apply(Object message) throws Exception {
			log.info("SLOW: Received request of type {}", message);
			if (message instanceof Task) {
				getSender().tell(message, getSelf());			
				Thread.sleep(1000);
			} else if (message instanceof Swap) {
				getContext().unbecome();
			}
		}
	};

	@Override
	public void onReceive(Object message) throws Exception {
		log.info("Received request of type {}", message);
		if (message instanceof Task) {
			getSender().tell(message, getSelf());
		} else if (message instanceof Swap) {
			getContext().become(slow);
		}

	}

	public static class Task implements Serializable {
		private final int id;
		public Task(int id) {
			this.id = id;
		}
		@Override
		public String toString() {
			return "Task{" + "id=" + id + '}';
		}
	}

	public static class Swap {
	}

	public static class Response implements Serializable {
	}
}
