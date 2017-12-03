package com.bizruntime.rx.obersvable.transform;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func2;

public class Map {

	static Logger log = Logger.getLogger(Window.class);

	public static void main(String[] args) {
		Map ex = new Map();
		ex.example();
	}

	void example() {
		Observable.range(1, 5)
				.map((str)->{
					
					return str;
				}).subscribe(System.out::println);

	}

}
