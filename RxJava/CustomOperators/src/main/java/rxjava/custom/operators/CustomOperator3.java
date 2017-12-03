package rxjava.custom.operators;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Observable.Operator;
import rx.Subscriber;
public class CustomOperator3 {
	private Logger log = Logger.getLogger(CustomOperator3.class);
	
	private void createOperator() {

		Operator<Boolean, Integer> isOdd = child -> {

			// Subscriber without param
			return new Subscriber<Integer>(child) {                   
		        public void onNext(Integer value) {
		            child.onNext((value & 1) != 0);
		        }
		        
		        public void onError(Throwable e) {
		            child.onError(e);
		        }
		        
		        public void onCompleted() {
		            child.onCompleted();
		        }
		    };
		};
		
		
		Observable.range(1, 2_000_000_000)
	    .lift(isOdd)
	    .take(2)
	    .subscribe(System.out::println);
		
	}

	public static void main(String[] args) {
		CustomOperator3 example = new CustomOperator3();
		example.createOperator();
	}

}
