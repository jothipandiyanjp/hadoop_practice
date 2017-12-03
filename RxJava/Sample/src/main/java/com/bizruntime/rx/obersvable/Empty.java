package com.bizruntime.rx.obersvable;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;

public class Empty {
	static Logger log = Logger.getLogger(Empty.class);

	public static void main(String[] args) {
		Empty ex = new Empty();
		List<String[]> list = new ArrayList<String[]>();
		// list.add("test");
		list.add(new String[] { "a", "b", "c" });

		Observable.from(new String[] { "a", "b", "c" }).subscribe(
				new Subscriber<Object>() {

					@Override
					public void onCompleted() {
						log.debug("Oncomplete");
					}

					@Override
					public void onError(Throwable arg0) {
						log.debug("error");

					}

					@Override
					public void onNext(Object x) {
						log.debug(x);
					}
				});

		Observable.from(new String[] { "a", "b", "c" }).subscribe(
				x -> log.debug(x),
				x -> log.debug("error " + x),
				() -> log.debug("completed --> array")

		);

		Observable.from(new String[] { "a", "b", "c" }).empty().subscribe(
				log::debug, 
				log::debug,
				() -> log.debug("completed --> empty")

		);
		Observable.from(new String[] { "a", "b", "c" }).never().subscribe(
				log::debug, 
				log::debug,
				() -> log.debug("completed --> never")

		);
		
		Observable.from(new String[] { "a", "b", "c" }).error(new Exception()).subscribe(
				log::debug, 
				log::debug,
				() -> log.debug("completed --> error")

		);

	}

}
