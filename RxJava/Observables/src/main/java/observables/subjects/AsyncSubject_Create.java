package observables.subjects;

import java.util.concurrent.TimeUnit;




import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;
import rx.subjects.AsyncSubject;
import rx.subjects.Subject;

public class AsyncSubject_Create {
	private Logger log = Logger.getLogger(AsyncSubject_Create.class);

	public  <T> Subscription subscribePrint(Observable<T> observable){
		
		return observable.subscribe((v)->log.debug(Thread.currentThread().getName()
				+ "|" + " : " +v),
			(e)->{
				log.debug("error while subscribing.."+e.getMessage());
				
			},
			() -> log.debug("zip completed")
			
		);
	}
	private void useCreateMethod() {

		Observable<Long> interval = Observable.interval(100L,
				TimeUnit.MILLISECONDS);

		Subject<Long, Long> publishSubject = AsyncSubject.create();	
		interval.subscribe(publishSubject);
		Subscription sub1 = subscribePrint(publishSubject);

		try {
			Thread.sleep(300L);

			publishSubject.onNext(555L);
			publishSubject.onNext(55L);
			Thread.sleep(500L);
		} catch (InterruptedException e) {
		}
		log.debug("Is publish subject has observers -> "+publishSubject.hasObservers());
		sub1.unsubscribe();

	}
	
	
	public static void main(String[] args) {
		AsyncSubject_Create c = new AsyncSubject_Create();
		c.useCreateMethod();
	}




}
