package com.bizruntime.rx.obersvable.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public class Buffer {
	static Logger log = Logger.getLogger(Buffer.class);

	public static void main(String[] args) {
		Buffer ex = new Buffer();
		ex.example();
	}

	void example() {
		subscribePrint(
				Observable.range(0,10).buffer(3),"Buffer"
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
