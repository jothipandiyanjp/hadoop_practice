package operators.connectable;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;


public class replay {
	private Logger log = Logger.getLogger(replay.class);


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
		
		ConnectableObservable<Long> published = interval.replay();

		Subscription sub1 = subscribePrint(published);
		
		Subscription sub2 = subscribePrint(published);

		published.connect();
		Subscription sub3 = null;
		try {
			Thread.sleep(300L);

			sub3 = subscribePrint(published);
			Thread.sleep(500L);
		} catch (InterruptedException e) {
		}
		
		sub1.unsubscribe();
		sub2.unsubscribe();
		sub3.unsubscribe();

		




	}
	public static void main(String[] args) {
		replay ex = new replay();
		ex.example();
	}

}
