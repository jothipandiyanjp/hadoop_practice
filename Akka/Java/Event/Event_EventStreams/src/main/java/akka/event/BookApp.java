package akka.event;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.DeadLetter;
import akka.actor.Props;

public class BookApp {

	public void pubAndSub() {
		final ActorSystem system = ActorSystem.create("book-publish-subscribe");

		String author = "JP";

		final ActorRef bookPublisher = system.actorOf(Props.create(BookPublisher.class), "book-publisher");

		final ActorRef subscriber1 = system.actorOf(Props.create(BookSubscriber.class), "subscriber1");
		
		final ActorRef subscriber2 = system.actorOf(Props.create(BookSubscriber.class), "subscriber2");

	
		Book book1 = new Book("Akka in Action", Arrays.asList(author, "Rob"));
		bookPublisher.tell(book1, ActorRef.noSender());
		
		
		system.eventStream().unsubscribe(subscriber2, Book.class);

		Book book2 = new Book("Akka in Action2", Arrays.asList("ROb"));
		//bookPublisher.tell(book2, ActorRef.noSender());

	}

	public static void main(String[] args) {
		BookApp app = new BookApp();
		app.pubAndSub();
	}
}
