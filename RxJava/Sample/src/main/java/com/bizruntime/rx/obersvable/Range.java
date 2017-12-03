package com.bizruntime.rx.obersvable;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;

public class Range {
	static Logger log = Logger.getLogger(Range.class);

	public static void main(String[] args) {
		Range ex = new Range();
		List<String[]> list = new ArrayList<String[]>();
		// list.add("test");
		list.add(new String[] { "a", "b", "c" });

		Observable.range(0,2).repeat()
		          .subscribe(
				log::debug, 
				log::debug,
				() -> log.debug("completed --> array"));
	
		Observable.range(0,2).repeat(0l)
        .subscribe(
		log::debug, 
		log::debug,
		() -> log.debug("completed --> array"));
}

}
