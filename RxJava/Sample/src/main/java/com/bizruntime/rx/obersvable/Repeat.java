package com.bizruntime.rx.obersvable;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;

public class Repeat {
	static Logger log = Logger.getLogger(Repeat.class);

	public static void main(String[] args) {
		Repeat ex = new Repeat();
		List<String[]> list = new ArrayList<String[]>();
		// list.add("test");
		list.add(new String[] { "a", "b", "c" });

		Observable.range(0, 3)
        .subscribe(
		log::debug, 
		log::debug,
		() -> log.debug("completed --> array"));


		Observable.range(10, 3)
		          .subscribe(
				log::debug, 
				log::debug,
				() -> log.debug("completed --> array"));

	}

}
