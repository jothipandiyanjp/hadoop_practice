package akka.example;

import java.io.Serializable;

public class Confirm implements Serializable {
	public final long deliveryId;

	public Confirm(long deliveryId) {
		this.deliveryId = deliveryId;
	}
}
