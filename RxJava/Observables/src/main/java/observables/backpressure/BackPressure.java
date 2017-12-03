package observables.backpressure;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;


public class BackPressure {
	private Logger log = Logger.getLogger(BackPressure.class);


	public  <T> Subscription subscribePrint(Observable<T> observable){
		
		return observable.subscribe((v)->log.debug(Thread.currentThread().getName()
				+ "|" + " : " +v),
			(e)->{
				log.debug("error while subscribing.."+e.getMessage());
				
			},
			() -> log.debug("zip completed")
			
		);
	}
	void example() {


		CountDownLatch latch = new CountDownLatch(7);
		
		Path path = Paths.get("src", "main", "resources");
		
		Observable<?> data = null;
		data=listFolderViaUsing(path, "*")
		.flatMap(file -> {
			if (!Files.isDirectory((Path) file, null)) {
				return fromViaUsing(file).subscribeOn(Schedulers.io());
			}
			
			return Observable.empty();
		});
		
		subscribePrint(
				data.sample(Observable.interval(
						100L, TimeUnit.MILLISECONDS).take(10)
						.concatWith(Observable.interval(
								200L, TimeUnit.MILLISECONDS)))
				.doOnCompleted(() -> latch.countDown()));

		subscribePrint(
				data.throttleLast(100L, TimeUnit.MILLISECONDS)
				.doOnCompleted(() -> latch.countDown()));
		Observable<Object> sampler = Observable.create(subscriber -> {
			subscriber.onNext(0);
			
			try {
				Thread.sleep(100L);

				subscriber.onNext(10);

				Thread.sleep(200L);

				subscriber.onNext(200);
				
				Thread.sleep(150L);
				subscriber.onCompleted();
			} catch (Exception e) {
				subscriber.onError(e);
			}
		}).repeat().subscribeOn(Schedulers.computation());
		

		 subscribePrint(
				data.sample(sampler).debounce(150L, TimeUnit.MILLISECONDS)
				.doOnCompleted(() -> latch.countDown()));
		

		 subscribePrint(
				data.buffer(2, 1500)
				.doOnCompleted(() -> latch.countDown()));
		

		 subscribePrint(
				data.window(3L, 200L, TimeUnit.MILLISECONDS).flatMap(o -> o)
				.doOnCompleted(() -> latch.countDown()));
		
		
		 subscribePrint(
				data.onBackpressureBuffer(10000)
				.doOnCompleted(() -> latch.countDown()));

		 subscribePrint(
				data.onBackpressureDrop()
				.doOnCompleted(() -> latch.countDown()));

		try {
			latch.await(15L, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}

		




	}
	private Observable<?> fromViaUsing(Object file) {
		// TODO Auto-generated method stub
		return null;
	}
	private Observable<?> listFolderViaUsing(Path path, String string) {
		// TODO Auto-generated method stub
		return null;
	}
	public static void main(String[] args) {
		BackPressure ex = new BackPressure();
		ex.example();
	}

}
