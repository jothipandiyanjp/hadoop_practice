package com.bizruntime.rx.obersvable.combininig;


import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;

public class ZipWithCombineLatest {
	private Logger log = Logger.getLogger(ZipWithCombineLatest.class);

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
	
		// 3 CombinedLatest
		Observable<String> greetings =slowDown(Observable.just("Hello", "Hi", "Howdy", "Zdravei", "Yo", "Good to see ya"), 1000);
		Observable<String> names =
				slowDown(Observable.just("Meddle", "Tanya", "Dali", "Joshua"), 1500L);

		Observable<String> punctuation =
				slowDown(Observable.just(".", "?", "!", "!!!", "..."), 1100L);

		Observable<String> combined = Observable.combineLatest(
				greetings, names, punctuation,
				(greeting, name, puntuation) -> greeting + " " + name + puntuation);

		blockingSubscribePrint(combined, "Sentences");

		
		}




	public static void main(String[] args) {
		ZipWithCombineLatest ex = new ZipWithCombineLatest();
		ex.example();
	}

}
