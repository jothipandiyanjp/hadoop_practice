package observables.blocking;


import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class Latest {
	 Logger log = Logger.getLogger(Latest.class);

	 
	 public  <T> Subscription subscribePrint(Observable<T> observable){
			
			return observable.subscribe((v)->log.debug(Thread.currentThread().getName()
					+ "|" + " : " +v),
				(e)->{
					log.debug("error while subscribing.."+e.getMessage());
					
				},
				() -> log.debug("zip completed")
				
			);
			
		}
	 
	public static void main(String[] args) {
		Latest c = new Latest();
		c.useCreateMethod();
	}

	private void useCreateMethod() {
		
		Iterable<Long> next = Observable
				.interval(100L, TimeUnit.MILLISECONDS)
				.toBlocking()
				.latest();
		Iterator<Long> iterator = next.iterator();
		log.debug(iterator.next());
		log.debug(iterator.next());
		log.debug(iterator.next());
		log.debug(iterator.next());
		try {
			Thread.sleep(5500L);
		} catch (InterruptedException e) {}
		log.debug(iterator.next());
		
	}

}
