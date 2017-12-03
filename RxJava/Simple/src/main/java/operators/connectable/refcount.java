package operators.connectable;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;


public class refcount {
	private Logger log = Logger.getLogger(refcount.class);


	public  <T> Subscription subscribePrint(Observable<T> observable){
		
		return observable.subscribe((v)->log.debug(Thread.currentThread().getName()
				+ "|" + " : " +v),
			(e)->{
				log.debug("error while subscribing.."+e.getMessage());
				
			},
			() -> log.debug("zip completed")
			
		);
	}
	void example() {

		Observable<Long> interval = Observable.interval(100L,
				TimeUnit.MILLISECONDS);
		Observable<Long> refCount = interval.publish().refCount() ;// interval().share();

		Subscription sub1 = subscribePrint(refCount);
		Subscription sub2 = subscribePrint(refCount);
		
		try {

			Thread.sleep(1000L);
		
		} catch (InterruptedException e) {
		}
		sub1.unsubscribe();
		sub2.unsubscribe();
		
	}
	public static void main(String[] args) {
		refcount ex = new refcount();
		ex.example();
	}

}
