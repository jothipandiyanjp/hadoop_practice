package observables.groupped;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;


public class GroupBy {
	private Logger log = Logger.getLogger(GroupBy.class);


	public  <T> Subscription subscribePrint(Observable<T> observable){
		
		return observable.subscribe((v)->log.debug(Thread.currentThread().getName()
				+ "|" + " : " +v),
			(e)->{
				log.debug("error while subscribing.."+e.getMessage());
				
			},
			() -> log.debug("zip completed")
			
		);
	}
	void example() {

		List<String> albums = Arrays.asList(
				"The Piper at the Gates of Dawn", "A Saucerful of Secrets", "More",
				"Ummagumma", "Atom Heart Mother", "Meddle", "Obscured by Clouds",
				"The Dark Side of the Moon", "Wish You Were Here", "Animals",
				"The Wall");

		Observable.from(albums).groupBy(album -> album.split(" ").length)
				.subscribe(obs -> {
					subscribePrint(obs);
				});

		Observable
				.from(albums)
				.groupBy(album -> album.replaceAll("[^mM]", "").length(),
						album -> album.replaceAll("[mM]", "*"))
				.subscribe(
						obs -> subscribePrint(obs));



	}
	public static void main(String[] args) {
		GroupBy ex = new GroupBy();
		ex.example();
	}

}
