package com.bizruntime.rx.obersvable.filter;


import org.apache.log4j.Logger;
import rx.Observable;

public class IgnoreElements {
	static Logger log = Logger.getLogger(IgnoreElements.class);

	public static void main(String[] args) {
		IgnoreElements ex = new IgnoreElements();
		ex.example();
	}

	void example() {
		subscribePrint(
				Observable.just(0,1,2,0,1,10,12),"Buffer"
				);
		
		
	}

	<T>void subscribePrint(Observable<Integer> observable, String name) {
		observable.skip(2).subscribe((v) -> System.out.println(name + " : " + v),
				(e) -> {
					log.debug("Error from " + name + ":");
					log.debug(e.getMessage());
				}, () -> log.debug(name + " ended!"));
	}

}
