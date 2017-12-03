package com.bizruntime.rx.obersvable.conditional;



import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;


public class DefaultIfEmpty {
	private Logger log = Logger.getLogger(DefaultIfEmpty.class);
	
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
		
		

		Observable<Object> test = Observable.empty().defaultIfEmpty(5);
		subscribePrint(test);

		}




	public static void main(String[] args) {
		DefaultIfEmpty ex = new DefaultIfEmpty();
		ex.example();
	}

}
