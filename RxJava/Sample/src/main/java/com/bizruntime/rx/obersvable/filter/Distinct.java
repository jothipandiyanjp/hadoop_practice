package com.bizruntime.rx.obersvable.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public class Distinct {
	static Logger log = Logger.getLogger(Distinct.class);

	public static void main(String[] args) {
		Distinct ex = new Distinct();
		ex.example();
	}

	void example() {
		subscribePrint(
				Observable.just(0,1,2,0,1,10).distinct().buffer(3),"Buffer"
				);
		
		
	}

	<T>void subscribePrint(Observable<T> observable, String name) {
		observable.subscribe((v) -> System.out.println(name + " : " + v),
				(e) -> {
					log.debug("Error from " + name + ":");
					log.debug(e.getMessage());
				}, () -> log.debug(name + " ended!"));
	}

}
