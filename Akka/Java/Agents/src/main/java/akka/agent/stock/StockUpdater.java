package akka.agent.stock;

import akka.actor.ActorSystem;
import akka.dispatch.Mapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class StockUpdater implements Runnable{
	ActorSystem sys = ActorSystem.create("AgentSystem");
	private final LoggingAdapter log = Logging.getLogger(sys, this);

	private int countDown = 5;
	private Stock stock;
	public StockUpdater(Stock stock) {
		this.stock = stock;
	}

	public void run() {
		while (countDown > 0) {
			try {
				Thread.sleep(75);
			} catch (InterruptedException e) {
			}
			String x = Thread.currentThread().getName();
			stock.getPrice().send(new Mapper<Float, Float>() {
				public Float apply(Float i) {
					return i + 10;
				}
			});
			log.debug("Quote update by thread (" + x
					+ "), current price " + stock.getPrice().get());
			countDown = countDown - 1;
		}
	}
	
}
