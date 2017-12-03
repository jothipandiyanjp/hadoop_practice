package akka.immutable.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImmutableMessage {

	
	private final int sequenceNumber;
	
	private final List<String> values;

	public ImmutableMessage(int sequenceNumber, List<String> values) {
		this.sequenceNumber = sequenceNumber;
		// List unmodifiable 
		this.values = Collections.unmodifiableList(new ArrayList<String>(values));
		
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public List<String> getValues() {
		return values;
	}
		
}
