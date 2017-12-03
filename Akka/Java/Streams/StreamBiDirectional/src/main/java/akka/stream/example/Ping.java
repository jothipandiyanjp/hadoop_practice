package akka.stream.example;

public class Ping implements Message{
	final int id;

	public Ping(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {

		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Ping)
			return ((Ping)obj).id == id;
		else
			return false;
	}

	@Override
	public String toString() {
		return "PIng [id=" + String.valueOf(id) + "]";
	}

	
}
