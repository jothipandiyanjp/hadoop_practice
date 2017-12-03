package com.spark.stream.examples;

import java.nio.ByteBuffer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.receiver.ActorHelper;
import org.apache.spark.streaming.receiver.ActorReceiver;
import org.apache.spark.streaming.receiver.ActorReceiverData;
import org.apache.spark.streaming.receiver.ActorSupervisorStrategy;
import org.apache.spark.util.AkkaUtils;
import org.slf4j.Logger;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.DefaultSupervisorStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import scala.Function0;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.reflect.ClassTag;

public class WithActorApp {

	
	
	class CustomActor extends UntypedActor{
		
		@Override
		public void onReceive(Object message) throws Exception {
			System.out.println(message);	
		}
	}
	public void wordCount() throws ClassNotFoundException {
	//	AkkaUtils.createActorSystem(arg0, arg1, arg2, arg3, arg4)
		SparkConf conf = new SparkConf().setAppName("fileStream").setMaster("local[3]");
		ActorSystem system = ActorSystem.create("akka");

		JavaStreamingContext jssc = new JavaStreamingContext(conf,Durations.seconds(2));
		
/*		new MyActor(Props.create(CustomActor.class),"actor1" , StorageLevel.MEMORY_ONLY(),
				 ActorSupervisorStrategy.defaultStrategy(), scala.reflect.ClassTag$.MODULE$.apply(String.class));
*/		
/*		JavaReceiverInputDStream<Object> lines = jssc.actorStream(Props.create(MyActor.class),
											"CustomReceiver");
*/
	//	ActorRef ref  = system.actorOf(Props.create(MyActor.class),"actor1");
//		ref.tell("Hi", ActorRef.noSender());
		jssc.start();
		jssc.awaitTermination();
	}

	public static void main(String[] args) throws ClassNotFoundException {
		WithActorApp app = new WithActorApp();
		app.wordCount();
	}
}
