package com.bizruntime.rx.obersvable.combininig;


import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;

public class Zip {
	private Logger log = Logger.getLogger(Zip.class);

	public  <T> void blockingSubscribePrint(Observable<T> observable, String name) {
		CountDownLatch latch = new CountDownLatch(1);
		subscribePrint(observable.doAfterTerminate(() -> latch.countDown()));
		try { latch.await(); } catch (InterruptedException e) {log.error("Interrupted error occured");}
	}
	
	public <T> Observable<T> slowDown(Observable<T> o, long ms) {
		return o.zipWith(Observable.interval(ms, TimeUnit.MILLISECONDS), (elem, i) -> {
			log.debug(i);
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
	
		
		// 1
		Observable<Integer>zip=Observable.zip(Observable.just(1, 2, 4,2,6),Observable.just(1, 3, 4,3),(x,y)->x+y);
	//	subscribePrint(zip);
	
		//2
		blockingSubscribePrint(slowDown(zip, 1000), "Timed zip");
	
			
		}




	public static void main(String[] args) {
		Zip ex = new Zip();
		ex.example();
	}

}
