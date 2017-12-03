package com.akka.persistent.view;





import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.SnapshotMetadata;
import akka.persistence.SnapshotOffer;
import akka.persistence.UntypedPersistentView;

public class PersistenceActorView extends UntypedPersistentView {
	final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public String persistenceId() {
		return "sample-id-1-view";
	}

	@Override
	public String viewId() {
		return "sample-id-1";
	}
	
	
	@Override
	public void onReceive(Object message) throws Exception {
	
		// 	
		if(isPersistent()){
			log.info("Persistent messgage -> "+message);
			SnapshotOffer snap =(SnapshotOffer)message;
			SnapshotMetadata metadata = snap.metadata();
			long sequenceNr = metadata.sequenceNr();
			log.info("sequenceNr -> "+sequenceNr);
			log.info("metadata -> " + metadata);
		}else if(message instanceof String)
			log.info("Instance of String ->"+message);
		else
			unhandled(message);
	}

}
