package com.bizruntime.rx.action;


import java.util.ArrayList;

import java.util.List;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.Subscriber;

public class ActionExample {
	static Logger log = Logger.getLogger(ActionExample.class);

	public static void main(String[] args) {
		ActionExample c = new ActionExample();
		c.useCreateMethod();
	}

	Integer a = 0;

	private void useCreateMethod() {
		List<Integer> number = new ArrayList<Integer>();
		number.add(1);
		number.add(2);
		number.add(3);
		number.add(4);
		
		
		// Using anonymous class   
		Observable.from(number).subscribe(
				new Action1<Integer>() {

					@Override
					public void call(Integer t) {
						System.out.println(t);
					}
				
				});
		
		Observable.from(number).subscribe(
				
				
				
				);
		
		
	}
	

}
