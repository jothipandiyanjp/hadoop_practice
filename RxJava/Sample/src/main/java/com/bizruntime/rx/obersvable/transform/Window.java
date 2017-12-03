package com.bizruntime.rx.obersvable.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public class Window {
	static Logger log = Logger.getLogger(Window.class);

	public static void main(String[] args) {
		Window ex = new Window();
		ex.example();
	}

	void example() {
		subscribePrint(
				Observable.range(0,10).window(3),"Buffer"
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
