package akka.event.example2;

public class Electronic implements AllKindsOfMusic {
	final public String artist;

	public Electronic(String artist) {
		this.artist = artist;
	}

	@Override
	public String toString() {
		return "Electronic [artist=" + artist + "]";
	}
	
	
}