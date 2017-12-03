package akka.agent.stock;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class StockReader implements Runnable{
	ActorSystem sys = ActorSystem.create("AgentSystem");
	private final LoggingAdapter log = Logging.getLogger(sys, this);

	private int countDown = 10;
	private Stock stock;
	public StockReader(Stock stock) {
		this.stock = stock;
		
	}

	public void run() {
		while (countDown > 0) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
			String x = Thread.currentThread().getName();
			Float stockTicker = stock.getPrice().get();
			log.debug("Quote read by thread (" + x+ "), current price " + stockTicker);
			countDown = countDown - 1;
		}
	}
}
