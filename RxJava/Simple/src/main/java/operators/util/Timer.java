package operators.util;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.schedulers.TimeInterval;
import rx.schedulers.Timestamped;

public class Timer {
	private Logger log = Logger.getLogger(Timer.class);

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
		List<Number> list = Arrays.asList(1, 2, 3);


		Observable<TimeInterval<Long>> timeInterval = Observable.timer(0L,
				150L, TimeUnit.MILLISECONDS).timeInterval();

		Subscription sub = subscribePrint(timeInterval);

		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
		}
		sub.unsubscribe();

	}

	public static void main(String[] args) {
		Timer ex = new Timer();
		ex.example();
	}

}
