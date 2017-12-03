package com.bizruntime.rx.obersvable;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;

public class Just {
	static Logger log = Logger.getLogger(Just.class);

	public static void main(String[] args) {
		Just ex = new Just();
		List<String[]> list = new ArrayList<String[]>();
		// list.add("test");
		list.add(new String[] { "a", "b", "c" });



		Observable.just( "a", "b", "c","a", "b", "c","a", "b", "c").subscribe(
				log::debug, 
				log::debug,
				() -> log.debug("completed --> array")

		);

	}

}
