package akka.circuit.breaker.example;

import java.util.concurrent.TimeUnit;

import scala.Function1;
import scala.PartialFunction;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.impl.Promise;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Status.Success;
import akka.actor.UntypedActor;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class CircuitBreakerApp {

	public void createTask() throws InterruptedException {

		ActorSystem system = ActorSystem.create("akka");
		Props serviceProps = Service.props();

//		final ActorRef service = system.actorOf( serviceProps, "Service" );

		
		final ActorRef service = system.actorOf(SimpleCircuitBreaker.props(serviceProps),"SimpleCircuitBreaker");

		ActorRef taskCreator = system.actorOf(TaskCreator.props(service),"TaskCreator");

		system.scheduler().schedule(Duration.create(0, TimeUnit.SECONDS),
				Duration.create(200, TimeUnit.MILLISECONDS), taskCreator,
				new Tick(), system.dispatcher(), ActorRef.noSender());
	    Thread.sleep( 10000 );

	    system.shutdown();
	    system.awaitTermination();

	}

	public static void main(String[] args) throws InterruptedException {
		CircuitBreakerApp app = new CircuitBreakerApp();
		app.createTask();
	}

	public static class TaskCreator extends UntypedActor {
		private LoggingAdapter log = Logging.getLogger(getContext().system(),
				this);

		private final ActorRef service;
		private int currentId = 0;

		public static Props props(ActorRef service) {
			return Props.create(TaskCreator.class, service);
		}

		public TaskCreator(ActorRef service) {
			this.service = service;
		}

		@Override
		public void onReceive(Object message) throws Exception {
			
			if (message instanceof Tick) {
				service.tell(new Service.Task(currentId++), getSelf());

			} else if (message instanceof Service.Task) {
				log.info("Received response {}", (message));
			}
		}

	}

	public static class Tick {

	}
}
