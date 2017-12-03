package com.bizruntime.util;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;

import rx.Notification;
import rx.Observable;
import rx.Producer;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DoOnEach {
	static Logger log = Logger.getLogger(DoOnEach.class);

	public static void main(String[] args) {
		DoOnEach ex = new DoOnEach();
		ex.example();
	}

	void example() {
		CountDownLatch latch = new CountDownLatch(1);

		Observable<Object> range = Observable
				.range(20, 5)
				.flatMap(n -> Observable
						.range(n, 3)
						.subscribeOn(Schedulers.computation())
						.doOnEach(debug("Source"))
						);
		
	}


	private <T> Action1<Notification<? super T>> debug(String description) {

		return (Notification<? super T> notification)->{
			if(notification.getKind().equals("OnNext")){
				log.info("NOtifiaction getvalue()"+notification.getValue());
			}
			if(notification.getKind().equals("OnError")){
				log.info("NOtifiaction error"+notification.getValue());
			}

		};
	}

	<Integer>void subscribePrint(Observable<Integer> observable, String name) {
		
		observable.onBackpressureDrop().subscribe((v) -> System.out.println(name + " : " + v),
				(e) -> {
					log.debug("Error from " + name + ":");
					log.debug(e.getMessage());
				}, () -> log.debug(name + " ended!"));
	}

}
