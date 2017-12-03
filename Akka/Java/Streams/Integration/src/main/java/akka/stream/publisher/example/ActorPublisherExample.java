package akka.stream.publisher.example;

import java.util.ArrayList;

import java.util.List;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.actor.AbstractActorPublisher;
import akka.stream.actor.ActorPublisherMessage;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class ActorPublisherExample {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer mat = ActorMaterializer.create(system);

	public static void main(String[] args) {
		ActorPublisherExample app = new ActorPublisherExample();
		app.publish();

	}

	public void publish() {
		final Source<JobManagerProtocol.Job, ActorRef> jobManagerSource = Source
				.actorPublisher(JobManager.props());

		final ActorRef ref = jobManagerSource
				.map(job -> job.payload.toUpperCase())
				.map(elem -> {
					log.debug(elem);
					return elem;
				}).to(Sink.ignore()).run(mat);

			ref.tell(new JobManagerProtocol.Job("a"), ActorRef.noSender());
			ref.tell(new JobManagerProtocol.Job("b"), ActorRef.noSender());	

			// send more message with loop and debug the output
			
	}

	public static class JobManagerProtocol {
		public static final JobAcceptedMessage JobAccepted = new JobAcceptedMessage();
		public static final JobDeniedMessage JobDenied = new JobDeniedMessage();

		final public static class Job {
			public final String payload;

			public Job(String payload) {
				this.payload = payload;
			}

		}

		public static class JobAcceptedMessage {
			@Override
			public String toString() {
				return "JobAccepted";
			}
		}

		public static class JobDeniedMessage {
			@Override
			public String toString() {
				return "JobDenied";
			}
		}

	}

	public static class JobManager extends
			AbstractActorPublisher<JobManagerProtocol.Job> {
		public static Props props() {
			return Props.create(JobManager.class);
		}

		private final int MAX_BUFFER_SIZE = 16;
		private final List<JobManagerProtocol.Job> buf = new ArrayList<JobManagerProtocol.Job>();

		public JobManager() {
			receive(ReceiveBuilder
					.match(JobManagerProtocol.Job.class,
							job -> buf.size() == MAX_BUFFER_SIZE,
							job -> {
								sender().tell(JobManagerProtocol.JobDenied,
										self());
							})
					.match(JobManagerProtocol.Job.class, job -> {
						sender().tell(JobManagerProtocol.JobAccepted, self());
						if (buf.isEmpty() && totalDemand() > 0) {
							onNext(job);
						} else {
							buf.add(job);
							deliverBuf();
						}
					})
					.match(ActorPublisherMessage.Request.class, request -> {
						deliverBuf();
					})
					.match(ActorPublisherMessage.Cancel.class,
							cancel -> context().stop(self())).build());
		}

		public void deliverBuf() {
			while (totalDemand() > 0) {
				if (totalDemand() <= Integer.MAX_VALUE) {

					final List<JobManagerProtocol.Job> took = buf.subList(0,
							Math.min(buf.size(), (int) totalDemand()));
					took.forEach(this::onNext);
					buf.removeAll(took);
					break;
				} else {
					final List<JobManagerProtocol.Job> took = buf.subList(0,
							Math.min(buf.size(), Integer.MAX_VALUE));
					
					took.forEach(this::onNext);
					buf.removeAll(took);
				}

			}

		}
	}
}
