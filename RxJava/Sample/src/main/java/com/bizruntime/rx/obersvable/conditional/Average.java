package com.bizruntime.rx.obersvable.conditional;


import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;

public class Average {
	static Logger log = Logger.getLogger(Average.class);

	public static void main(String[] args) {
		Average ex = new Average();
		ex.example();
	}

	void example() {
		subscribePrint(
				Observable.just(0,1,2,0,11,10),"Buffer"
				);
		
		
	}

	<T>void subscribePrint(Observable<Integer> observable, String name) {
		observable.all(x -> x<20).subscribe(new Subscriber<Boolean>() {
		@Override
		public void onCompleted() {
			// TODO Auto-generated method stub
			
		}
		@Override
			public void onError(Throwable e) {
				// TODO Auto-generated method stub
				
			}
		@Override
			public void onNext(Boolean t) {
				log.debug("element is less than 20 "+t);
			}
		});
	}

}
