package akka.example;

import java.io.Serializable;

public class MsgConfirmed implements Serializable {
	public final long deliveryId;

	public MsgConfirmed(long deliveryId) {
		this.deliveryId = deliveryId;
	}
}
