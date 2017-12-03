package com.bizruntime.rx.obersvable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public class IntervalExample {
	static Logger log = Logger.getLogger(IntervalExample.class);

	public static void main(String[] args) {
		IntervalExample ex = new IntervalExample();
		ex.example();
	}

	void example() {
		subscribePrint(
				Observable.interval(500L, TimeUnit.MILLISECONDS),
				"Interval Observable"
				);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	<T>void subscribePrint(Observable<T> observable, String name) {
		observable.subscribe((v) -> System.out.println(name + " : " + v),
				(e) -> {
					log.debug("Error from " + name + ":");
					log.debug(e.getMessage());
				}, () -> log.debug(name + " ended!"));
	}

}
