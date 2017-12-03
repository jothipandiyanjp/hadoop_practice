package com.akka.stream.example;

import java.io.File;
import java.math.BigInteger;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;

public class SourceApp {
	private final ActorSystem system = ActorSystem.create("akka-stream");
	private final LoggingAdapter log  = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	
	
	public void source(){
		
		Source<Integer, NotUsed> source = Source.range(1, 100);
		
		// After doing some operations on source then only source starts executing. Here we  use runXXX().
		source.runForeach(i ->  log.debug("i -> "+i), materializer);

		
		// scan(T zero, FUnction2<T,Integer,T> f)  Similar to `fold` but is not a terminal operation

		final Source<BigInteger, NotUsed> factorial = source.scan(BigInteger.ONE,
													(acc, next)  ->  acc.multiply(BigInteger.valueOf(next)));
		

		factorial.map(num -> ByteString.fromString(num.toString()+"\n"))
				.runWith(FileIO.toFile(new File("factorials.txt")),materializer);
		
		
	}
	
	public static void main(String[] args) {
		SourceApp app = new SourceApp();
		app.source();
	
	}
}
