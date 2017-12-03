package com.bizruntime.rx.obersvable.combininig;


import org.apache.log4j.Logger;

import rx.Observable;

public class BlockingObservable {
	static Logger log = Logger.getLogger(BlockingObservable.class);

	public static void main(String[] args) {
		BlockingObservable ex = new BlockingObservable();
		ex.example();
	}

	void example() {
		subscribePrint(
				Observable.just(0,1,2,0,11,10),"Buffer"
				);
		
		
	}

	<T>void subscribePrint(Observable<Integer> observable, String name) {
	/*	Iterator<Integer> it=observable.next().iterator();
		while(it.hasNext())
			log.debug(it.next());
	*/}

}
