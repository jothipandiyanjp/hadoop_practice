package com.bizruntime.rx.obersvable.conditional;



import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;


public class TakeWhile {
	private Logger log = Logger.getLogger(TakeWhile.class);

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
				

		
		blockingSubscribePrint(words.delay(800L, TimeUnit.MILLISECONDS).takeWhile(
				word -> word.length() > 2), "takeWhile");

		}




	public static void main(String[] args) {
		TakeWhile ex = new TakeWhile();
		ex.example();
	}

}
