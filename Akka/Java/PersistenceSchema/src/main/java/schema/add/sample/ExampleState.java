package schema.add.sample;

import java.io.Serializable;

import java.util.ArrayList;


public class ExampleState implements Serializable{
	
    private static final long serialVersionUID = 1L;
    private final ArrayList<SeatReserved> events;
	
    public ExampleState() {
    	this(new ArrayList<SeatReserved>());
    	
	}
    public ExampleState(ArrayList<SeatReserved> events) {
		this.events = events;
	}

    public ExampleState copy(){
    	return new ExampleState(new ArrayList<SeatReserved>(events));
    }
    

    public ArrayList<SeatReserved> getEvents() {
		return events;
	}
	public void update(SeatReserved evt) {
        events.add(evt);
    }
 
    public int size() {
        return events.size();
    }
 
    @Override
    public String toString() {
        return events.toString();
    }
	
	
}
