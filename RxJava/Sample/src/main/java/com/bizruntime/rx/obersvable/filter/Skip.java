package com.bizruntime.rx.obersvable.filter;


import org.apache.log4j.Logger;
import rx.Observable;

public class Skip {
	static Logger log = Logger.getLogger(Skip.class);

	public static void main(String[] args) {
		Skip ex = new Skip();
		ex.example();
	}

	void example() {
		subscribePrint(
				Observable.just(0,1,2,0,1,10,12),"Buffer"
				);
		
		
	}

	<T>void subscribePrint(Observable<Integer> observable, String name) {
		observable.ignoreElements().subscribe((v) -> System.out.println(name + " : " + v),
				(e) -> {
					log.debug("Error from " + name + ":");
					log.debug(e.getMessage());
				}, () -> log.debug(name + " ended!"));
	}

}
