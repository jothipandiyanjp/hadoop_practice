package com.bizruntime.rx.obersvable;


import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

public class Create {
	static Logger log = Logger.getLogger(Create.class);

	public static void main(String[] args) {
		Create c = new Create();
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
		Observable.create(new OnSubscribe<Integer>() {
			public void call(Subscriber<? super Integer> t) {

				number.stream().forEach(x -> {
					a = x + a;
				});
				
				if (!t.isUnsubscribed())
					t.onNext(a);
			}

		}).subscribe(log::debug, log::debug,
				() -> log.debug("completed --> addition"));

		
		// Using lambda
		Observable.create(sub->{
			number.stream().forEach(x -> a = x + a);
			
			if (!sub.isUnsubscribed())
				sub.onNext(a);
		}).subscribe(log::debug, log::debug,
				() -> log.debug("completed --> addition"));
		
	}
	

}
