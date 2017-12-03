package akka.event.example2;

public class Jazz implements AllKindsOfMusic {

	final public String artist;

	public Jazz(String artist) {
		this.artist = artist;
	}

	@Override
	public String toString() {
		return "Jazz [artist=" + artist + "]";
	}
	
}
