package akka.stream.example;

public class Pong implements Message{
	final int id;

	public Pong(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {

		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pong)
			return ((Pong)obj).id == id;
		else
			return false;
	}

	@Override
	public String toString() {
		return "Pong [id=" + String.valueOf(id) + "]";
	}

	
}
