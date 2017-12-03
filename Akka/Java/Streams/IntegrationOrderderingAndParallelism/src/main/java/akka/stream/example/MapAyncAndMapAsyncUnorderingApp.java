package akka.stream.example;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.ActorMaterializerSettings;
import akka.stream.javadsl.Source;

public class MapAyncAndMapAsyncUnorderingApp {
	private final ActorSystem system = ActorSystem.create();
	private final  LoggingAdapter log = Logging.getLogger(system, this);
	
	static class SometimesSlowService {
		private final Executor ec;
		private final ActorSystem system = ActorSystem.create();
		private final  LoggingAdapter log = Logging.getLogger(system, this);
		
		public SometimesSlowService(Executor ec) {
			this.ec = ec;
		}

		private final AtomicInteger runningCount = new AtomicInteger();

		public CompletionStage<String> convert(String s) {
			log.debug("running: " + s + "("
					+ runningCount.incrementAndGet() + ")");
			return CompletableFuture.supplyAsync(
					() -> {
						if (!s.isEmpty() && Character.isLowerCase(s.charAt(0)))
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
							}
						else
							try {
								Thread.sleep(20);
							} catch (InterruptedException e) {
							}
						log.debug("completed: " + s + "("
								+ runningCount.decrementAndGet() + ")");
						return s.toUpperCase();
					}, ec);
		}
	}
	
	
	
	public void mapAsyncUnordered(){
		final Executor blockingEc = system.dispatchers().lookup("blocking-dispatcher");
		final SometimesSlowService service = new SometimesSlowService(blockingEc);
		final ActorMaterializer mat = ActorMaterializer.create(
				  ActorMaterializerSettings.create(system).withInputBuffer(4, 4), system);
		Source.from(Arrays.asList("a", "B", "C", "D", "e", "F", "g", "H", "i", "J"))
		  .map(elem -> { log.debug("before: " + elem); return elem; })
		  .mapAsyncUnordered(4, service::convert)
		  .runForeach(elem -> log.debug("after: " + elem), mat);

		
		
	}
	
	public void mapAsync(){
		final Executor blockingEc = system.dispatchers().lookup("blocking-dispatcher");
		final SometimesSlowService service = new SometimesSlowService(blockingEc);
		final ActorMaterializer mat = ActorMaterializer.create(
				  ActorMaterializerSettings.create(system).withInputBuffer(4, 4), system);
		Source.from(Arrays.asList("a", "B", "C", "D", "e", "F", "g", "H", "i", "J"))
		  .map(elem -> { log.debug("before: " + elem); return elem; })
		  .mapAsync(4, service::convert)
		  .runForeach(elem -> log.debug("after: " + elem), mat);

		
		
	}
	public static void main(String[] args) {
		MapAyncAndMapAsyncUnorderingApp app = new MapAyncAndMapAsyncUnorderingApp();
		app.mapAsync();
	//	app.mapAsyncUnordered();
	}
}
