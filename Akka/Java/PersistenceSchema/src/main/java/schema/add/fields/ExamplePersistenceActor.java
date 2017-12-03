package schema.add.fields;

import java.util.Arrays;

import schema.add.sample.SeatReserved;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import akka.persistence.SnapshotOffer;
import akka.persistence.UntypedPersistentActor;

public class ExamplePersistenceActor extends UntypedPersistentActor{
	final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
    private ExampleState state = new ExampleState();

	public String persistenceId() {
		return "sample-id";
	}


    public int getNumEvents() {
        return state.size();

    }

    @Override
	public void onReceiveCommand(Object message) throws Exception {

		if(message instanceof SeatReserved){

			final SeatReserved data = ((SeatReserved)message);
			
            persistAll(Arrays.asList(data), new Procedure<SeatReserved>() {
            	public void apply(SeatReserved evt) throws Exception {
            		state.update(evt);
            	}
			});
		}else if(message.equals("snap")){
        //    saveSnapshot(state.copy());
        }else if (message.equals("print")) {
        	log.info("State -> "+state);
        }else {        	
            unhandled(message);
          }
	}

	@Override
	public void onReceiveRecover(Object message) throws Exception {		
		log.info("recover "+message);
		if(message instanceof SeatReserved){	
			
			state.update((SeatReserved)message);
		}
		else if(message instanceof SnapshotOffer){
		//	state = (ExampleState) ((SnapshotOffer)message).snapshot();
		}
		else{
			unhandled(message);
		}
	}

	
}
