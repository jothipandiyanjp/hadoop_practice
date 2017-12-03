package sample.remote.calculator;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.AddressFromURIString;
import akka.actor.Deploy;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.remote.RemoteScope;
import akka.remote.routing.RemoteRouterConfig;
import akka.routing.FromConfig;
import akka.routing.RoundRobinPool;

import com.typesafe.config.ConfigFactory;

public class LookupApplication {
	private final ActorSystem system = ActorSystem.create("CalculatorSystem",
			ConfigFactory.load(("calculator")));
	private final LoggingAdapter log = Logging.getLogger(system, this);

	public static void main(String[] args) {
		if (args.length == 0)
			new LookupApplication().startRemoteCalculatorSystem();

	}

	public void startRemoteCalculatorSystem() {

		Address addr = new Address("akka.tcp", "CalculatorSystem",
				"192.168.1.193", 2552);

		ActorRef ref = system.actorOf(Props.create(CalculatorActor.class)
				.withDeploy(new Deploy(new RemoteScope(addr))), "calculator");

		ref.tell("msg", null);
		ref.tell("msg", null);
		ref.tell("msg", null);
		ref.tell("msg", null);
		ref.tell("msg", null);
		ref.tell("msg", null);
		ref.tell("msg", null);

		Address[] addresses = {
				new Address("akka.tcp", "CalculatorSystem", "192.168.1.193",
						2552),
				AddressFromURIString
						.parse("akka.tcp://CalculatorSystem@192.168.1.9:2555") };

		ActorRef routerRemote = system.actorOf(new RemoteRouterConfig(
				new RoundRobinPool(2), addresses).props(Props
				.create(CalculatorActor.class)));

		routerRemote.tell("mesg1", null);

		routerRemote.tell("mesg1", null);

		routerRemote.tell("mesg1", null);

		routerRemote.tell("mesg1", null);
		routerRemote.tell("mesg1", null);



		ref.tell("msg", null);
		log.info("Actot Path " + ref.path());

	}

}
