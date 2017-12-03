package com.bizruntime.rx.obersvable.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public class ThrottleWithTimeout {
	static Logger log = Logger.getLogger(ThrottleWithTimeout.class);

	public static void main(String[] args) {
		ThrottleWithTimeout ex = new ThrottleWithTimeout();
		ex.example();
	}

	void example() {
		subscribePrint(
				Observable.range(0,100),"Buffer"
				);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	<T>void subscribePrint(Observable<T> observable, String name) {
		observable.debounce(1,TimeUnit.NANOSECONDS).subscribe((v) -> System.out.println(name + " : " + v),
				(e) -> {
					log.debug("Error from " + name + ":");
					log.debug(e.getMessage());
				}, () -> log.debug(name + " ended!"));
	}

}
