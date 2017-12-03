package operators.error.handling;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;


public class onExceptionResumeNext {
	private Logger log = Logger.getLogger(onExceptionResumeNext.class);


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
		
		Observable<String> numbers = Observable.just("1", "2", "three", "4", "5");
		Observable<Integer> n = numbers.map(Integer::parseInt).onErrorReturn(e -> -1);

		Observable<Integer> defaultOnError = Observable.just(5, 4, 3, 2, 1);

		n = numbers.map(Integer::parseInt).onExceptionResumeNext(defaultOnError);

		subscribePrint(n);


	}
	public static void main(String[] args) {
		onExceptionResumeNext ex = new onExceptionResumeNext();
		ex.example();
	}

}
