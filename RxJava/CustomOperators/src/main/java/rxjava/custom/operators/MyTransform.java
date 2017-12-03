package rxjava.custom.operators;

import rx.Observable;
import rx.Observable.Transformer;

public class MyTransform<Integer,String> implements Transformer<Integer,String> { 

	public MyTransform() {
	}


	public Observable<String> call(Observable<Integer> source) {
		return null;
	}
}
