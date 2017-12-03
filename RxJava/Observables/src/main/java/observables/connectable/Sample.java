package observables.connectable;

import org.apache.log4j.Logger;
import rx.Observable;
import rx.observables.ConnectableObservable;

public class Sample {
	private Logger log = Logger.getLogger(Sample.class);


	void example() {

		ConnectableObservable<Integer> firstMillion  = Observable.range( 1, 1000000 ).sample(7, java.util.concurrent.TimeUnit.MILLISECONDS).publish();

		firstMillion.subscribe(
				 (v) -> log.debug("Subscriber #1:" + v),       // onNext
				  (e)->  log.debug("Error: " + e), 				// onError
				  ()-> log.debug("Sequence #1 complete")       // onCompleted
				);
		firstMillion.subscribe(
				 (v) -> log.debug("Subscriber #2:" + v),       // onNext
				  (e)->  log.debug("Error: " + e), 				// onError
				  ()-> log.debug("Sequence #2 complete")       // onCompleted
				);
		
		firstMillion.connect();
	}
	public static void main(String[] args) {
		Sample ex = new Sample();
		ex.example();
	}

}
