package operators.async;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.util.async.Async;

public class start {
	static Logger log = Logger.getLogger(start.class);

	public static void main(String[] args) {
		start c = new start();
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
	

		
		// Using lambda
		Observable obs=Async.start(
				()-> {log.debug("start method called");
					return number;
				}
		);
		
		
	}
	

}
