package observables.blocking;


import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;

public class FirstOrDefault {
	 Logger log = Logger.getLogger(FirstOrDefault.class);

	public static void main(String[] args) {
		FirstOrDefault c = new FirstOrDefault();
		c.useCreateMethod();
	}

	private void useCreateMethod() {
		Observable.just(1, 2, 3)
        .firstOrDefault(10)
        .subscribe(new Subscriber<Integer>() {
      @Override
      public void onNext(Integer item) {
         log.debug("Next: " + item);
      }

      @Override
      public void onError(Throwable error) {
         log.debug("Error: " + error.getMessage());
      }

      @Override
      public void onCompleted() {
         log.debug("Sequence complete.");
      }
  });		
	}

}
