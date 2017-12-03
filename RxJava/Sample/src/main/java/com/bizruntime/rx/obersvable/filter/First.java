package com.bizruntime.rx.obersvable.filter;


import org.apache.log4j.Logger;

import rx.Observable;

public class First {
	static Logger log = Logger.getLogger(First.class);

	public static void main(String[] args) {
		First ex = new First();
		ex.example();
	}

	void example() {
		subscribePrint(
				Observable.just(0,1,2,0,11,10),"Buffer"
				);
		
		
	}

	<T>void subscribePrint(Observable<Integer> observable, String name) {
		observable.filter(t->{ return t>10;}).subscribe((v) ->log.debug(name + " : " + v),
				(e) -> {
					log.debug("Error from " + name + ":");
					log.debug(e.getMessage());
				}, () -> log.debug(name + " ended!"));
	}

}
