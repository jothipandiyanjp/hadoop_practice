package akka.cluster.ddata.example;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.cluster.Cluster;
import akka.cluster.ddata.DistributedData;
import akka.cluster.ddata.Flag;
import akka.cluster.ddata.FlagKey;
import akka.cluster.ddata.GSet;
import akka.cluster.ddata.GSetKey;
import akka.cluster.ddata.Key;
import akka.cluster.ddata.ORSet;
import akka.cluster.ddata.ORSetKey;
import akka.cluster.ddata.PNCounter;
import akka.cluster.ddata.PNCounterKey;
import akka.cluster.ddata.Replicator;
import akka.cluster.ddata.Replicator.Changed;
import akka.cluster.ddata.Replicator.Subscribe;
import akka.cluster.ddata.Replicator.Update;
import akka.cluster.ddata.Replicator.UpdateResponse;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class Replicator_Update extends AbstractActor {

	private static String TICK = "TICK";

	private final LoggingAdapter log = Logging.getLogger(context().system(),
			this);

	private final ActorRef replicator = DistributedData.get(context().system())
			.replicator();

	private final Cluster node = Cluster.get(context().system());

	// Creating keys
	final Key<PNCounter> counter1Key = PNCounterKey.create("counter1");
	final Key<GSet<String>> set1Key = GSetKey.create("set1");
	final Key<ORSet<String>> set2Key = ORSetKey.create("set2");
	final Key<Flag> activeFlagKey = FlagKey.create("active");

	public Replicator_Update() {
		receive(ReceiveBuilder
				.match(String.class, a -> a.equals(TICK), a -> receiveTick())
//				.match(Changed.class, c -> c.key().equals(set2Key),c -> receiveChanged((Changed<ORSet<String>>) c))
				.match(Changed.class, c -> c.key().equals(counter1Key),c -> {
					
					PNCounter incrementedValue = (PNCounter) c.dataValue();
					log.info("PNCounter value : "+incrementedValue.value());
					
				})
				.match(UpdateResponse.class, r -> receiveUpdateResponse())
				.build());
	}

	private void receiveTick() {
		replicator.tell(
				new Replicator.Update<PNCounter>(counter1Key, PNCounter
						.create(), Replicator.writeLocal(), curr -> curr
						.increment(node, 1)), self());
	}

	private void receiveChanged(Changed<ORSet<String>> c) {
		ORSet<String> data = c.dataValue();
		
		log.info("Current elements: {}", data.getElements());
	}

	private void receiveUpdateResponse() {
	}

	@Override
	public void preStart() throws Exception {
		Subscribe<PNCounter> subscribe = new Subscribe<>(counter1Key, self());
		replicator.tell(subscribe, ActorRef.noSender());
	}

	private final Cancellable tickTask = context()
			.system()
			.scheduler()
			.schedule(Duration.create(5, TimeUnit.SECONDS),
					Duration.create(5, TimeUnit.SECONDS), self(), TICK,
					context().dispatcher(), self());

	@Override
	public void postStop() throws Exception {
		tickTask.cancel();
	}

}
