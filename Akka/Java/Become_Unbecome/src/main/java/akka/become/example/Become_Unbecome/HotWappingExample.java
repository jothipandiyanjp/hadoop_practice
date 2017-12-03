package akka.become.example.Become_Unbecome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import static akka.become.example.Become_Unbecome.Messages.*;

public class HotWappingExample {
	
	void demo(){
		ActorSystem system = ActorSystem.create();
	    ActorRef[] chopsticks = new ActorRef[5];
	    
	    for (int i = 0; i < 5; i++)
	        chopsticks[i] = system.actorOf(Props.create(Chopstick.class), "Chopstick" + i);

	    List<String> names = Arrays.asList("Ghosh", "Boner", "Klang", "Krasser", "Manie");
	    List<ActorRef> hakkers = new ArrayList<>();
	    int i = 0;

	    for (String name: names) {
	        hakkers.add(system.actorOf(Props.create(Hacker.class, name, chopsticks[i], chopsticks[(i + 1) % 5])));
	        i++;
	      }
	    
	    hakkers.stream().forEach(hakker -> hakker.tell(Think, ActorRef.noSender()));
	    
	}
	
	public static void main(String[] args) {
		HotWappingExample example = new HotWappingExample();
		example.demo();
	}
}
