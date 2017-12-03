package com.bizruntime.rx.obersvable;

import org.apache.log4j.Logger;

import rx.Observable;

public class Defer {
	 Logger log = Logger.getLogger(Create.class);

		public static void main(String[] args) {
			Defer c = new Defer();
			c.useCreateMethod();
		}


		private void useCreateMethod() {
			SomeType sm=new SomeType();

			Observable<String> observable=sm.valueObservable();
			sm.setValue("10");

			observable.subscribe(log::debug);
		}

}


 class SomeType{
	 private String value;

	public void setValue(String value) {
		this.value = value;
	}

	  public Observable<String> valueObservable(){
		  
		  return Observable.defer(()->Observable.just(value));
	  }
}