package com.spark.stream.examples;

import com.google.common.io.Closeables;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.receiver.Receiver;

import scala.Tuple2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

import com.google.common.io.Closeables;

import scala.Tuple2;


public class JavaCustomReceiver extends Receiver<String> {
	private String host;
	private int port;

	public JavaCustomReceiver(String host, int port) {
		super(StorageLevel.MEMORY_AND_DISK());
		this.host = host;
		this.port = port;
	}

	public void receive() {
		try {
			Socket socket = null;
			BufferedReader reader = null;
			String userInput = null;
			try {
				socket = new Socket(host, port);
				reader = new BufferedReader(new InputStreamReader(
						socket.getInputStream(), StandardCharsets.UTF_8));
				while (!isStopped() && (userInput = reader.readLine()) != null) {
					System.out.println("Received data '" + userInput + "'");
					store(userInput);
				}
			} finally {
				Closeables.close(reader, /* swallowIOException = */true);
				Closeables.close(socket, /* swallowIOException = */true);
			}
	
			restart("Trying to connect again");
		} catch (ConnectException ce) {
			restart("Could not connect", ce);
		} catch (Throwable t) {
			restart("Error receiving data", t);
		}
	}

	@Override
	public void onStart() {
		
		new Thread() {
			@Override
			public void run() {
				receive();
			}
		}.start();
	}

	@Override
	public void onStop() {
	}

	public void custom() {
		SparkConf sparkConf = new SparkConf().setAppName("JavaCustomReceiver")
				.setMaster("local[*]");
		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf,
				new Duration(2000));

		JavaReceiverInputDStream<String> lines = jssc.receiverStream(this);


	    JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
	      public Iterable<String> call(String x) {

	        return Arrays.asList(x.split(" "));
	      }
	    });
	    JavaPairDStream<String, Integer> wordCounts = words.mapToPair(
	      new PairFunction<String, String, Integer>() {
	         public Tuple2<String, Integer> call(String s) {
	          return new Tuple2<String,Integer>(s, 1);
	        }
	      }).reduceByKey(new Function2<Integer, Integer, Integer>() {
	        
	        public Integer call(Integer i1, Integer i2) {
	          return i1 + i2;
	        }
	      });

		wordCounts.print();
		jssc.start();
		jssc.awaitTermination();

	}

	public static void main(String[] args) {
		JavaCustomReceiver app = new JavaCustomReceiver("localhost", 9999);
		app.custom();
	}
}
