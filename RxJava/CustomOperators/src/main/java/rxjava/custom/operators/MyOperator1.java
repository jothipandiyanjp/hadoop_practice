package rxjava.custom.operators;


import rx.Observable.Operator;
import rx.Subscriber;

public class MyOperator1 implements Operator<Boolean, Integer> {

	public Subscriber<Integer> call(final Subscriber<? super Boolean> child) {

		return   new Subscriber<Integer>() {                   
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
		
		}


}
