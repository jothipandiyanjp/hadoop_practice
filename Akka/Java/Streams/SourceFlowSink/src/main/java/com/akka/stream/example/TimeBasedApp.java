package com.akka.stream.example;

import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;

public class TimeBasedApp {

	private final ActorSystem system = ActorSystem.create("akka-stream");
	private final LoggingAdapter log = Logging.getLogger(system , TimeBasedApp.class);
	private final Materializer materializer = ActorMaterializer.create(system);

	public Source<BigInteger, NotUsed> source() {

		Source<Integer, NotUsed> source = Source.range(1, 100);

		final Source<BigInteger, NotUsed> factorial = source.scan( BigInteger.ONE, (acc, next) -> {
				// modify second and check backpressure
				//Thread.sleep(2000);
				
				return acc.multiply(BigInteger.valueOf(next));
			});
		return factorial;
	}

	private void usingThrottle(Source<BigInteger, NotUsed> factorial) {

		// if incoming stream is slow it also slows down

		factorial.zipWith(Source. range(0, 99),  (num, idx) -> String.format("%d! = %s", idx, num))
						.throttle(4, Duration.create(10, TimeUnit.SECONDS), 5, ThrottleMode.shaping())
						.runForeach(s -> log.debug(s), materializer);
	

	}

	public static void main(String[] args) {
		TimeBasedApp app = new TimeBasedApp();
		app.usingThrottle(app.source());
	}
}