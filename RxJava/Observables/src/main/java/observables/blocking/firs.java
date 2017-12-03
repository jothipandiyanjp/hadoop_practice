package observables.blocking;


import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;

public class firs {
	 Logger log = Logger.getLogger(firs.class);

	public static void main(String[] args) {
		firs c = new firs();
		c.useCreateMethod();
	}

	private void useCreateMethod() {
		Observable.just(1, 2, 3)
        .first()
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
