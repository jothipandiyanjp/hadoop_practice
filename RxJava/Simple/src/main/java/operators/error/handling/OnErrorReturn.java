package operators.error.handling;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;


public class OnErrorReturn {
	private Logger log = Logger.getLogger(OnErrorReturn.class);


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
		subscribePrint(n);

	}
	public static void main(String[] args) {
		OnErrorReturn ex = new OnErrorReturn();
		ex.example();
	}

}
