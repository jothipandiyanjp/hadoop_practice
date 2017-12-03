package observables.blocking;


import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class Next {
	 Logger log = Logger.getLogger(Next.class);

	 
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
		Next c = new Next();
		c.useCreateMethod();
	}

	private void useCreateMethod() {
		
		Iterable<Long> next = Observable
				.interval(100L, TimeUnit.MILLISECONDS)
				.toBlocking()
				.next();
		Iterator<Long> iterator = next.iterator();
		log.debug(iterator.next());
		log.debug(iterator.next());
		log.debug(iterator.next());
		log.debug(iterator.next());
		
	}

}
