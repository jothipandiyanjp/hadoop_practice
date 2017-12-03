package com.bizruntime.util;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import rx.Observable;


public class Sample {
	Logger log=Logger.getLogger(Sample.class);
	public static void main(String[] args) {
		Sample myapp=new Sample();
		myapp.hello1();
		
	}
	
	private void hello1() {
		List<String> tweets=Arrays.asList("Hi..","Hello..!","Hello....:)");
        Observable<String> observable = Observable.from(tweets);
        observable.subscribe(log::debug);
	}
	
	
}
