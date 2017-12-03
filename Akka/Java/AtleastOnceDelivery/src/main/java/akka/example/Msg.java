package akka.example;

import java.io.Serializable;

public class Msg implements Serializable {
	public final long deliveryId;
	public final String s;

	public Msg(long deliveryId, String s) {
		this.deliveryId = deliveryId;
		this.s = s;
	}
}
