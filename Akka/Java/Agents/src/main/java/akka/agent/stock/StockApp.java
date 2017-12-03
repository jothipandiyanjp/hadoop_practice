package akka.agent.stock;

import scala.concurrent.ExecutionContext;
import akka.actor.ActorSystem;
import akka.agent.Agent;
import akka.dispatch.ExecutionContexts;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class StockApp {
	ActorSystem sys = ActorSystem.create("AgentSystem");
	private final LoggingAdapter log = Logging.getLogger(sys, this);
	private void updateAndGetStockDetails() {
		ExecutionContext ex = ExecutionContexts.global();
		Stock stock =new Stock("YHOO", Agent.create(new Float("400.25"), ex));
		Thread[] readerThreads = new Thread[10];
		Thread[] updateThreads = new Thread[5];
		for (int i = 0; i < 10; i++) {
			readerThreads[i] = new Thread(new StockReader(stock));
			readerThreads[i].setName("#" + i);
		}
		for (int i = 0; i < 5; i++) {
			updateThreads[i] = new Thread(new StockUpdater(stock));
			updateThreads[i].setName("#" + i);
		}
		
		for (int i = 0; i < 10; i++)
			readerThreads[i].start();

		for (int i = 0; i < 5; i++)
			updateThreads[i].start();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			log.error("Sleep Interrupted Exception "+e);
		}
		
		sys.terminate();
		
	}

	public static void main(String[] args) {
		StockApp app = new StockApp();
		app.updateAndGetStockDetails();
	}

}
