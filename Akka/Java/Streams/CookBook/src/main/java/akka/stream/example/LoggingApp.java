package akka.stream.example;

import java.util.Arrays;

import scala.Function1;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.function.Procedure;
import akka.stream.ActorMaterializer;
import akka.stream.Attributes;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import akka.stream.scaladsl.Sink;

public class LoggingApp {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	final Source<Integer, NotUsed> mySource = Source.range(1, 10);

	public void usingPrintln() {		
		mySource.map(elem -> {
			System.out.println(elem);
			return elem;
		}).async().runForeach(i -> {}, materializer);

	}
	
	public void usingLogWithAttributes() {

        final int onElement = Logging.DebugLevel();
        final int onFinish = Logging.InfoLevel();
        final int onFailure = Logging.ErrorLevel();
        
		mySource.log("before-map")
				.withAttributes(Attributes.createLogLevels(onElement, onFinish, onFailure))
				.map(elem -> {
			return elem;
		}).async().runForeach(i -> {}, materializer);

	}
	public void usingLogWithLoggingAdapter() {

     	mySource.log("before-map",log)
				.map(elem -> {
			return elem;
		}).async().runForeach(i -> {}, materializer);

	}




	public static void main(String[] args) {
		LoggingApp example = new LoggingApp();
//		example.usingPrintln();
//		example.usingLogWithAttributes();
		example.usingLogWithLoggingAdapter();
	}
}
