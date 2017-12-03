package akka.example;

import java.io.Serializable;

public class MsgSent implements Serializable {
	public final String s;

	public MsgSent(String s) {
		this.s = s;
	}
}
