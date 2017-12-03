package operators.util;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;

public class WithoutSubscribeOn {
	private Logger log = Logger.getLogger(WithoutSubscribeOn.class);

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
		WithoutSubscribeOn aService = new WithoutSubscribeOn();

		 Observable<String> ob1 = aService.getData();
		 
		    CountDownLatch latch = new CountDownLatch(1);
		 
		    ob1.subscribe(s -> {
		    	try {
					Thread.sleep(200);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
		        log.info("Got {}"+ s);
		    }, e -> log.error(e.getMessage(), e), () -> latch.countDown());
		 
		    try {
				latch.await();
			} catch (InterruptedException e1) {
				log.error(e1.getMessage());
			}
	}

	public static void main(String[] args) {
		WithoutSubscribeOn ex = new WithoutSubscribeOn();
		ex.example();
	}

}
