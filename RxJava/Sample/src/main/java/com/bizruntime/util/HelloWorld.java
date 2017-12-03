package com.bizruntime.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.functions.Action1;

public class HelloWorld {
	Logger log=Logger.getLogger(HelloWorld.class);
	public static void main(String[] args) {
		HelloWorld myapp=new HelloWorld();
		myapp.hello1("Jothipandiyan","Richard");
		myapp.hello2("Jothipandiyan","Richard");
	       ExecutorService es = Executors.newSingleThreadExecutor();
	     // Future future= es.submit(hello3());
	}
	private static Runnable hello3() {
		return null;
	}
	private void hello2(String... names) {
		Observable.from(names).subscribe((name)->log.debug("Hello..! "+name));	
	}
	
	private void hello1(String... names) {
		Observable.from(names).subscribe(new Action1<String>() {
			@Override
			public void call(String name) {
				log.debug("Hello..! "+name);
			}
		});
		
		}
	
	
}
