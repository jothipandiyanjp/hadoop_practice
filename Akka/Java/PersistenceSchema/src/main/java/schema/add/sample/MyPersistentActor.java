package schema.add.sample;

import org.apache.log4j.Logger;

import akka.persistence.UntypedPersistentActor;

public class MyPersistentActor extends UntypedPersistentActor{

	private final static Logger LOG =Logger.getLogger(MyPersistentActor.class);

	
	
	public String persistenceId() {
		return "persistent-1";
	}
	
	@Override
	public void onReceiveCommand(Object msg) throws Exception {
		if(msg instanceof SeatReserved)
			LOG.debug(msg);
		else
			unhandled(msg);
	}
	
	@Override
	public void onReceiveRecover(Object msg) throws Exception {
		
	}
	
}
