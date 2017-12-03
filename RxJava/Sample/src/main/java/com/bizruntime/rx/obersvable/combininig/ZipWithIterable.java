package com.bizruntime.rx.obersvable.combininig;


import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import rx.Observable;

public class ZipWithIterable {
	private Logger log = Logger.getLogger(ZipWithIterable.class);


	

	void example() {
	
		Observable<Integer> data1=Observable.just(1, 3, 4,5,6);
		Observable<Integer> data2=Observable.just(1, 3, 4,3);
		List<Observable<Integer>> iter=Arrays.asList(data1,data2);

		Observable.zip(iter, args1 -> args1).subscribe((arg)->{
			  for (Object o : arg) {
			    log.debug(o);
			  }
			});
		}




	public static void main(String[] args) {
		ZipWithIterable ex = new ZipWithIterable();
		ex.example();
	}

}
