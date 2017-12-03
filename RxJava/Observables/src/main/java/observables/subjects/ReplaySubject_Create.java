package observables.subjects;

import java.util.concurrent.TimeUnit;


import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

public class ReplaySubject_Create {
	private Logger log = Logger.getLogger(ReplaySubject_Create.class);

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

		Subject<Long, Long> publishSubject = ReplaySubject.create(5);	
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
		ReplaySubject_Create c = new ReplaySubject_Create();
		c.useCreateMethod();
	}




}
