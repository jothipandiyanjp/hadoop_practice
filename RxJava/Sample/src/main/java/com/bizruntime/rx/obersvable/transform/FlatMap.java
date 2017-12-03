package com.bizruntime.rx.obersvable.transform;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func2;

public class FlatMap {

	static Logger log = Logger.getLogger(Window.class);

	public static void main(String[] args) {
		FlatMap ex = new FlatMap();
		ex.example();
	}

	
	void example() {
		List<String> list=new ArrayList<String>();
		list.add("test");
		list.add("test");
		list.add("test");
		
		Observable.from(list)
				.flatMap((str)->{
					str +="+";
	                String str1 =str + "+";
	                String str2 = str1 + "+";

					return Observable.just(str,str1,str2);
				}).subscribe(System.out::println);

	}

}
