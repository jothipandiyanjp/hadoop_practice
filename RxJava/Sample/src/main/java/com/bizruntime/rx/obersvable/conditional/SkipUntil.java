package com.bizruntime.rx.obersvable.conditional;



import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;


public class SkipUntil {
	private Logger log = Logger.getLogger(SkipUntil.class);

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
				
		Observable<String> words1 = Observable.just("Some", "Other").zipWith(Observable.interval(500, TimeUnit.MILLISECONDS), (x, y) -> x);

		
		blockingSubscribePrint(words1.skipUntil(words).delay(1L, TimeUnit.MILLISECONDS), "takeUntil");

		}




	public static void main(String[] args) {
		SkipUntil ex = new SkipUntil();
		ex.example();
	}

}
