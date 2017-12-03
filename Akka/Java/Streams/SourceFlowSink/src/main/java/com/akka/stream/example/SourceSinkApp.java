package com.akka.stream.example;

import java.io.File;
import java.math.BigInteger;
import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;

public class SourceSinkApp {
	private final ActorSystem system = ActorSystem.create("akka-stream");
	private final LoggingAdapter log  = Logging.getLogger(system, this);
	private final Materializer materializer = ActorMaterializer.create(system);

	
	public Sink<String, CompletionStage<IOResult>> linkSink(String fileName){		

		return Flow.of(String.class)
					.map(s -> ByteString.fromString(s.toString()+ "\n" ) )
					.toMat(FileIO.toFile(new File(fileName)), Keep.right());	
	}
	

	public void sink(Source<BigInteger, NotUsed> factorial){
		factorial.map(BigInteger :: toString)
		.runWith(linkSink("factorial2.txt"),materializer);

	}
	
	
	public Source<BigInteger, NotUsed> source(){
		
		Source<Integer, NotUsed> source = Source.range(1, 100);
			
		final Source<BigInteger, NotUsed> factorial = source.scan(BigInteger.ONE,
													(acc, next)  -> { 
														
														
															Thread.sleep(2000);
														return acc.multiply(BigInteger.valueOf(next));});

		return factorial;
	}
	
	public static void main(String[] args) {
		SourceSinkApp app = new SourceSinkApp();
		// source
		Source<BigInteger, NotUsed> factorial = app.source();
		// sink
		app.sink(factorial);

	}
}
