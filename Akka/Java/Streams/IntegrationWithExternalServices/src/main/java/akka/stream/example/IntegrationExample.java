package akka.stream.example;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.stream.javadsl.Source;

public class IntegrationExample {

	public static class AddressSystem {
		public CompletionStage<Optional<String>> lookupEmail(String handle) {
			System.out.println("handle " + handle);
			return CompletableFuture.completedFuture(Optional.of(handle
					+ "@gmail.com"));
		}

		public CompletionStage<Optional<String>> lookupPhoneNumber(String handle) {
			return CompletableFuture.completedFuture(Optional.of(""
					+ handle.hashCode()));
		}
	}

	static class Email {
		public final String to;
		public final String title;
		public final String body;

		public Email(String to, String title, String body) {
			this.to = to;
			this.title = title;
			this.body = body;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			Email email = (Email) o;

			if (body != null ? !body.equals(email.body) : email.body != null) {
				return false;
			}
			if (title != null ? !title.equals(email.title)
					: email.title != null) {
				return false;
			}
			if (to != null ? !to.equals(email.to) : email.to != null) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			int result = to != null ? to.hashCode() : 0;
			result = 31 * result + (title != null ? title.hashCode() : 0);
			result = 31 * result + (body != null ? body.hashCode() : 0);
			return result;
		}
	}

	static class TextMessage {
		public final String to;
		public final String body;

		TextMessage(String to, String body) {
			this.to = to;
			this.body = body;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			TextMessage that = (TextMessage) o;

			if (body != null ? !body.equals(that.body) : that.body != null) {
				return false;
			}
			if (to != null ? !to.equals(that.to) : that.to != null) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			int result = to != null ? to.hashCode() : 0;
			result = 31 * result + (body != null ? body.hashCode() : 0);
			return result;
		}

	}

	static class EmailServer {
		public final ActorRef probe;

		public EmailServer(ActorRef probe) {
			this.probe = probe;
		}

		public CompletionStage<Email> send(Email email) {
			System.out.println(Thread.currentThread().getName());
			probe.tell(email.to, ActorRef.noSender());
			return CompletableFuture.completedFuture(email);
		}

	}

	static class Save {
		public final Tweet tweet;

		Save(Tweet tweet) {
			this.tweet = tweet;
		}
	}

	public static class Tweet {
		public final Author author;
		public final long timestamp;
		public final String body;

		public Tweet(Author author, long timestamp, String body) {
			this.author = author;
			this.timestamp = timestamp;
			this.body = body;
		}

		public Set<Hashtag> hashtags() {

			return Arrays.asList(body.split(" ")).stream()
					.filter(a -> a.startsWith("#")).map(a -> new Hashtag(a))
					.collect(Collectors.toSet());
		}

		@Override
		public String toString() {
			return "Tweet(" + author + "," + timestamp + "," + body + ")";
		}
	}

	public static class Author {
		public final String handle;

		public Author(String handle) {
			this.handle = handle;
		}

		@Override
		public String toString() {
			return "Author(" + handle + ")";
		}
	}

	public static class Hashtag {
		public final String name;

		public Hashtag(String name) {
			this.name = name;
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Hashtag other = (Hashtag) obj;
			return name.equals(other.name);
		}
	}

	public static class SourceTweets{
		final static Source<Tweet, NotUsed> tweets = Source.from(Arrays
				.asList(new Tweet[] {
						new Tweet(new Author("rolandkuhn"), System
								.currentTimeMillis(), "#akka rocks!"),
						new Tweet(new Author("patriknw"), System
								.currentTimeMillis(), "#akka !"),
						new Tweet(new Author("bantonsson"), System
								.currentTimeMillis(), "#akka !"),
						new Tweet(new Author("drewhk"), System
								.currentTimeMillis(), "#akka !"),
						new Tweet(new Author("ktosopl"), System
								.currentTimeMillis(), "#akka on the rocks!"),
						new Tweet(new Author("mmartynas"), System
								.currentTimeMillis(), "wow #akka !"),
						new Tweet(new Author("akkateam"), System
								.currentTimeMillis(), "#akka rocks!"),
						new Tweet(new Author("bananaman"), System
								.currentTimeMillis(), "#bananas rock!"),
						new Tweet(new Author("appleman"), System
								.currentTimeMillis(), "#apples rock!"),
						new Tweet(new Author("drama"), System
								.currentTimeMillis(),
								"we compared #apples to #oranges!") }));
	}
}
