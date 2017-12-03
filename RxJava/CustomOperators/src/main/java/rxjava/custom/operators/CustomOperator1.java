package rxjava.custom.operators;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Observable.Operator;
import rx.Subscriber;
public class CustomOperator1 {
	private Logger log = Logger.getLogger(CustomOperator1.class);
	
	private void createOperator() {		
		Observable.range(1, 2_000_000_000)
	    .lift(new MyOperator1())
	    .take(2)
	    .subscribe(System.out::println);
		
	}

	public static void main(String[] args) {
		CustomOperator1 example = new CustomOperator1();
		example.createOperator();
	}

}
