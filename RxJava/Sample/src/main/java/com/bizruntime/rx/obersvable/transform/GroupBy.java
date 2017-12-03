package com.bizruntime.rx.obersvable.transform;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;

public class GroupBy {

	static Logger log = Logger.getLogger(Window.class);

	public static void main(String[] args) {
		GroupBy ex = new GroupBy();
		ex.example();
	}

	void example() {
		List<Object> list=new ArrayList<Object>();
		list.add(1);
		list.add("a");
		list.add(2);
		list.add("b");
		list.add(3);
		list.add("c");
		
		Observable.from(list).groupBy(new Func1<Object,Object>() {

			@Override
			public Object call(Object t) {
				
				return t instanceof String;
			}
			
			
		}).subscribe(
				(x)->{
							log.debug(x);
						
					
				},	
				(x)->log.debug(x),
				()->log.debug("Completed")
				);	
		}

}
