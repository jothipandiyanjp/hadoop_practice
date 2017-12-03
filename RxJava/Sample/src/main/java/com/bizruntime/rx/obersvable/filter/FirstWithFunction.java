package com.bizruntime.rx.obersvable.filter;


import org.apache.log4j.Logger;
import rx.Observable;

public class FirstWithFunction {
	static Logger log = Logger.getLogger(FirstWithFunction.class);

	public static void main(String[] args) {
		FirstWithFunction ex = new FirstWithFunction();
		ex.example();
	}

	void example() {
		subscribePrint(
				Observable.just(0,1,2,0,11,10,12),"Buffer"
				);
		
		
	}

	<T>void subscribePrint(Observable<Integer> observable, String name) {
		observable.first(t->{ return t>10;}).subscribe((v) -> System.out.println(name + " : " + v),
				(e) -> {
					log.debug("Error from " + name + ":");
					log.debug(e.getMessage());
				}, () -> log.debug(name + " ended!"));
	}

}
