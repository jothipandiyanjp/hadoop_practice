package akka.event;

import java.util.List;

public class Book {
	private String book;
	private List<String> authors;
	public Book(String book, List<String> authors) {
		this.book = book;
		this.authors = authors;
	}
	@Override
	public String toString() {
		return "Book [book=" + book + ", authors=" + authors + "]";
	}
	
}
