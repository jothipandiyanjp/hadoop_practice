package observables.blocking;


import java.util.List;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;

public class Single {
	 Logger log = Logger.getLogger(Single.class);

	public static void main(String[] args) {
		Single c = new Single();
		c.useCreateMethod();
	}

	private void useCreateMethod() {
		List<Integer> in=Observable.just(1,1).toList().toBlocking().single();
		
         log.debug("Single: " + in);
      	}

}
