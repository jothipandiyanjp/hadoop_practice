package operators.error.handling;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;


public class RetryWhen {
	private Logger log = Logger.getLogger(RetryWhen.class);

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

		Observable<Integer> when = Observable.create(new ErrorEmitter()).retryWhen(attempts -> {
			return attempts.flatMap(error -> {
				if (error instanceof FooException) {
					System.err.println("Delaying...");
					return Observable.timer(1L, TimeUnit.SECONDS, Schedulers.immediate());
				}
				
				return Observable.error(error);
			});
		}).retry((attempts, error) -> {
			return (error instanceof BooException) && attempts < 3;
		});

		subscribePrint(when);

	}
	public static void main(String[] args) {
		RetryWhen ex = new RetryWhen();
		ex.example();
	}
	class ErrorEmitter implements OnSubscribe<Integer> {

		private int throwAnErrorCounter = 5;
		
		@Override
		public void call(Subscriber<? super Integer> subscriber) {
			subscriber.onNext(1);
			subscriber.onNext(2);

			if (throwAnErrorCounter > 4) {
				throwAnErrorCounter--;
				subscriber.onError(new FooException());
				return;
			}
			
			if (throwAnErrorCounter > 0) {
				throwAnErrorCounter--;
				subscriber.onError(new BooException());
				return;
			}

			subscriber.onNext(3);
			subscriber.onNext(4);
			subscriber.onCompleted();

		}
	}
	class FooException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public FooException() {
			super("Foo!");
		}
	}

	class BooException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public BooException() {
			super("Boo!");
		}
	}
	

}
