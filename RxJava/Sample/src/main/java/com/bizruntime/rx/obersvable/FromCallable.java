package com.bizruntime.rx.obersvable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;

public class FromCallable {
	 Logger log = Logger.getLogger(FromCallable.class);

	public static void main(String[] args) {
		FromCallable ex = new FromCallable();
		ex.example();
	}

	private void example() {
		List<String[]> list = new ArrayList<String[]>();

		list.add(new String[] { "a", "b", "c" });
		
		List<String[]> list1 = new ArrayList<String[]>();
		list1.add(new String[] { "a", "b", "c" });
		
		
		Observable.fromCallable(new Callable<Observable<String[]>>() {
			public Observable<String[]> call() throws Exception {
				
					
				
				return Observable.from(list).ambWith(Observable.from(list1));
			}
		}).subscribe((x)->log.debug(x),
				(x)->log.debug(x),
				()->log.debug("completed")
				);
	}

}
