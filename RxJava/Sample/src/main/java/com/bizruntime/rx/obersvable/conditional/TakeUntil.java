package com.bizruntime.rx.obersvable.conditional;



import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;


public class TakeUntil {
	private Logger log = Logger.getLogger(TakeUntil.class);

	public  <T> void blockingSubscribePrint(Observable<T> observable, String name) {
		CountDownLatch latch = new CountDownLatch(1);
		subscribePrint(observable.doAfterTerminate(() -> latch.countDown()));
		try { latch.await(); } catch (InterruptedException e) {log.error("Interrupted error occured");}
	}
	
	public <T> Observable<T> slowDown(Observable<T> o, long ms) {
		return o.zipWith(Observable.interval(ms, TimeUnit.MILLISECONDS), (elem, i) -> {
			//log.debug(i);
			return elem;});
	}

	
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
		
		
		Observable<String> words = Observable.just("one", "way", "or", "another", "I'll", "learn", "RxJava");
				

		Observable<String> words1 = Observable.just("Some", "Other").zipWith(Observable.interval(0l, TimeUnit.MILLISECONDS), (x, y) -> x);
		
		blockingSubscribePrint(words.takeUntil(words1).delay(1L, TimeUnit.SECONDS), "takeUntil");


		}




	public static void main(String[] args) {
		TakeUntil ex = new TakeUntil();
		ex.example();
	}

}
