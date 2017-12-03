package com.bizruntime.rx.obersvable.transform;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;

public class BufferTimeWithCOunt {
	static Logger log = Logger.getLogger(BufferTimeWithCOunt.class);

	public static void main(String[] args) {
		BufferTimeWithCOunt ex = new BufferTimeWithCOunt();
		ex.example();
	}

	void example() {
		subscribePrint(
				Observable.range(0,1000).buffer(500l,TimeUnit.NANOSECONDS,2),"Buffer"
				);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
