package observables.subjects;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class PublishSubscribers_MultipleSubscribers {
	private Logger log = Logger.getLogger(PublishSubscribers_MultipleSubscribers.class);

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

		Subject<Long, Long> publishSubject = PublishSubject.create();	
		interval.subscribe(publishSubject);
		Subscription sub1 = subscribePrint(publishSubject);
		Subscription sub2 = subscribePrint(publishSubject);
		Subscription sub3 = null;

		try {
			Thread.sleep(300L);

			publishSubject.onNext(555L);
			publishSubject.onNext(55L);
			sub3 = subscribePrint(publishSubject);

			Thread.sleep(500L);
		} catch (InterruptedException e) {
		}
		log.debug("Is publish subject has observers -> "+publishSubject.hasObservers());
		sub1.unsubscribe();
		sub2.unsubscribe();
		sub3.unsubscribe();

		
	}
	
	
	public static void main(String[] args) {
		PublishSubscribers_MultipleSubscribers c = new PublishSubscribers_MultipleSubscribers();
		c.useCreateMethod();
	}




}
