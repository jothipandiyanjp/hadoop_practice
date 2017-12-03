package com.bizruntime.samza;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;

public class MessageFromKafka implements StreamTask,WindowableTask{
	  private static final SystemStream OUTPUT_STREAM = new SystemStream("kafka", "kafka-output");

	
	  private int eventsSeen = 0;
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {
		 
		
		collector.send(new OutgoingMessageEnvelope(OUTPUT_STREAM,envelope.getMessage()));
		eventsSeen++;
	}


	public void window(MessageCollector collector, TaskCoordinator arg1)
			throws Exception {
		
		 collector.send(new OutgoingMessageEnvelope(OUTPUT_STREAM, eventsSeen));
		    eventsSeen = 0;	
	}

}
