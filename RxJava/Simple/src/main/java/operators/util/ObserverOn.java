package operators.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class ObserverOn {
	private Logger log = Logger.getLogger(ObserverOn.class);


    private static final Logger logger = Logger.getLogger(ObserverOn.class);
    private ExecutorService executor1 = Executors.newFixedThreadPool(5);
 
    Observable<String> getData() {
		 
		return Observable.<String>create(s -> {
			log.info("Start: Executing a Service");
		for (int i = 1; i <= 3; i++) {
		try {
			Thread.sleep(200);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.info("Emitting {} root " + i);
		s.onNext("root " + i);
		}
		log.info("End: Executing a Service");
		s.onCompleted();
		});
	}

	void example() {
    	ObserverOn aService = new ObserverOn();

    	Observable<String> ob1 = aService.getData();
 
        CountDownLatch latch = new CountDownLatch(1);
 
        ob1.observeOn(Schedulers.from(executor1)).subscribe(s -> {
        	try {
    			Thread.sleep(200);
    		} catch (Exception e) {
    			log.error(e.getMessage());
    		}
        	log.info("Got {}"+ s);
        }, e -> logger.error(e.getMessage(), e), () -> latch.countDown());
 
        try {
			latch.await();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public static void main(String[] args) {
		ObserverOn ex = new ObserverOn();
		ex.example();
	}

}
