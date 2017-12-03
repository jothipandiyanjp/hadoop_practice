package com.bizruntime.rx.obersvable.transform;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func2;

public class Scan {

	static Logger log = Logger.getLogger(Window.class);

	public static void main(String[] args) {
		Scan ex = new Scan();
		ex.example();
	}

	void example() {
		Observable.just(1, 2, 3, 4, 5)
				.scan(new Func2<Integer, Integer, Integer>() {
					@Override
					public Integer call(Integer sum, Integer item) {
						return sum + item;
					}
				}).subscribe(new Subscriber<Integer>() {
					@Override
					public void onNext(Integer item) {
						log.debug("Next: " + item);
					}

					@Override
					public void onError(Throwable error) {
						System.err.println("Error: " + error.getMessage());
					}

					@Override
					public void onCompleted() {
						log.debug("Sequence complete.");
					}
				});

	}

}
