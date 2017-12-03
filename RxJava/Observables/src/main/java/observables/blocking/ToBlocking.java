package observables.blocking;


import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class ToBlocking {
	 Logger log = Logger.getLogger(ToBlocking.class);

	 
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
		ToBlocking c = new ToBlocking();
		c.useCreateMethod();
	}

	private void useCreateMethod() {
		Observable
		.interval(100L, TimeUnit.MILLISECONDS)
		.take(5)
		.toBlocking()
		.forEach(log::debug);
	}

}
