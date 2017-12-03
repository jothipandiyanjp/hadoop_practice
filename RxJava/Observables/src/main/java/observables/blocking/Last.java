package observables.blocking;


import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class Last {
	 Logger log = Logger.getLogger(Last.class);

	 
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
		Last c = new Last();
		c.useCreateMethod();
	}

	private void useCreateMethod() {
		Integer first = Observable.range(3, 13).toBlocking().last();
		log.debug(first);
	}

}
