package com.bizruntime.util;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Producer;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

public class OnBackPressure {
	static Logger log = Logger.getLogger(OnBackPressure.class);

	public static void main(String[] args) {
		OnBackPressure ex = new OnBackPressure();
		ex.example();
	}

	void example() {
		List<Integer> list=new ArrayList<Integer>();
		list.add(1);
		list.add(2);list.add(3);list.add(4);list.add(5);
		list.add(1);
		list.add(2);list.add(3);list.add(4);list.add(5);		list.add(1);
		list.add(2);list.add(3);list.add(4);list.add(5);

				Observable obs=Observable.create(new OnSubscribe<Integer>() {
					@Override
					public void call(Subscriber<? super java.lang.Integer> t) {
						t.setProducer(new Producer() {
							
							@Override
							public void request(long n) {
								
							}
						});
					}});		
	}


	<Integer>void subscribePrint(Observable<Integer> observable, String name) {
		
		observable.onBackpressureDrop().subscribe((v) -> System.out.println(name + " : " + v),
				(e) -> {
					log.debug("Error from " + name + ":");
					log.debug(e.getMessage());
				}, () -> log.debug(name + " ended!"));
	}

}
