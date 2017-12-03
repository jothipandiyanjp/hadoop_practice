package operators.util;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.schedulers.Timestamped;

public class TimeStamp {
	private Logger log = Logger.getLogger(TimeStamp.class);


    

	void example() {
		Observable<Timestamped<Long>> observable = Observable.interval(1000,TimeUnit.MILLISECONDS).timestamp();

	        observable.subscribe(x -> log.debug(x));
	        
	        	log.debug("Press any key to unsubscribe");
	        
	        	Scanner sc=new Scanner(System.in);
	        	sc.next();
	        	
	        	log.debug("Press any key to exit");
	        	sc.next();

	}

	public static void main(String[] args) {
		TimeStamp ex = new TimeStamp();
		ex.example();
	}

}
