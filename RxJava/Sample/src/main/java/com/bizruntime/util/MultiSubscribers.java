package com.bizruntime.util;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import rx.Observable;


public class MultiSubscribers {
	Logger log=Logger.getLogger(MultiSubscribers.class);
	public static void main(String[] args) {
		MultiSubscribers myapp=new MultiSubscribers();
		myapp.hello1();
		
	}
	
	private void hello1() {
		List<String> tweets=Arrays.asList("Hi..","Hello..!","Hello....:)");
        Observable<String> observable = Observable.from(tweets);
        observable.subscribe(tweet -> log.debug("Subscriber 1 -> "+tweet));
        observable.subscribe(tweet -> log.debug("Subscriber 2 --> "+tweet));
        observable.subscribe(tweet -> log.debug("Subscriber 3 --->"+tweet));
        
	}
	
	
}
