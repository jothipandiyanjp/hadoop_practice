package rxjava.string;

import rx.Observable;
import rx.observables.StringObservable;

public class Sample {
	public static void main(String[] args) {
		
		
		Sample sample=new Sample();
		sample.getStringObject();
	}

	private void getStringObject() {
		StringObservable sob=new  StringObservable();
		sob.stringConcat(Observable.just("This is Bangelore"));
		
	}
	
}
