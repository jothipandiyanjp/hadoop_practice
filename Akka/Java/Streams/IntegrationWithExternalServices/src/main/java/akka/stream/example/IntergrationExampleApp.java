package akka.stream.example;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.example.IntegrationExample.AddressSystem;
import akka.stream.example.IntegrationExample.Author;
import akka.stream.example.IntegrationExample.Email;
import akka.stream.example.IntegrationExample.EmailServer;
import akka.stream.example.IntegrationExample.Hashtag;
import akka.stream.example.IntegrationExample.SourceTweets;
import akka.stream.example.IntegrationExample.Tweet;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;


public class IntergrationExampleApp {
	private final ActorSystem system = ActorSystem.create();
	private final LoggingAdapter log = Logging.getLogger(system, this);
	private final Materializer mat = ActorMaterializer.create(system);

	final Executor blockingEc = system.dispatchers().lookup("blocking-dispatcher");

	public static void main(String[] args) {
		IntergrationExampleApp app = new IntergrationExampleApp();
		app.withMapASync();
		//app.withMapASyncUnordered();
		
	}

	public void withMapASync() {
		
		final AddressSystem addressSystem = new AddressSystem();
//		final ActorRef probe = system.actorOf(Props.create(Database.class), "probe");

		final Source<Tweet, NotUsed> tweets = 	SourceTweets.tweets;

		ActorRef probe = system.actorOf(Props.create(PrintMessageActor.class));
		final ActorRef ref = system.actorOf(Props.create(DatabaseService.class,probe), "db");

		final EmailServer emailServer = new EmailServer(ref);

		final Hashtag AKKA = new Hashtag("#akka");
		
		final Source<Author, NotUsed> authors = tweets.filter(
					t ->  t.hashtags().contains(AKKA)).map(t -> t.author);

		final Source<String, NotUsed> emailAddresses = authors
					.mapAsync(4, author -> addressSystem.lookupEmail(author.handle))
					.filter(o ->o.isPresent()).map(o -> o.get());

			final RunnableGraph<NotUsed> sendEmails = emailAddresses.mapAsync(
					4,address ->  CompletableFuture.supplyAsync(() ->
					emailServer.send(new Email(address, "Akka",
							"I like your tweet")),blockingEc)).to(Sink.ignore());
	
		sendEmails.run(mat);
		

	}
public void withMapASyncUnordered() {
		
		final AddressSystem addressSystem = new AddressSystem();
//		final ActorRef probe = system.actorOf(Props.create(Database.class), "probe");

		final Source<Tweet, NotUsed> tweets = 	SourceTweets.tweets;

		ActorRef probe = system.actorOf(Props.create(PrintMessageActor.class));
		final ActorRef ref = system.actorOf(Props.create(DatabaseService.class,probe), "db");

		final EmailServer emailServer = new EmailServer(ref);

		final Hashtag AKKA = new Hashtag("#akka");
		
		final Source<Author, NotUsed> authors = tweets.filter(
					t ->  t.hashtags().contains(AKKA)).map(t -> t.author);

		final Source<String, NotUsed> emailAddresses = authors
					.mapAsyncUnordered(4, author -> addressSystem.lookupEmail(author.handle))
					.filter(o ->o.isPresent()).map(o -> o.get());

			final RunnableGraph<NotUsed> sendEmails = emailAddresses.mapAsync(
					4,address -> emailServer.send(new Email(address, "Akka",
							"I like your tweet"))).to(Sink.ignore());
	
		sendEmails.run(mat);
		

	}
	
}
	
