package com.bizruntime.akka;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.dispatch.Futures;
import akka.japi.Creator;
import akka.japi.Option;
import scala.concurrent.Future;

public class TypedActorDocTest  {

	
	
	static public interface Squarer { 
		void squareDontCare(int i);
		Future<Integer> square(int i);
		Option<Integer> squareNowPlease(int i);
		int squareNow(int i);
		
	}
	
	static class SquarerImpl  implements Squarer {
		private String name;
		
		public SquarerImpl() {
			this.name = "default";
		}
		
		public SquarerImpl(String name) {
			super();
			this.name = name;
			
			// Creating hierarchies using Context
			createHierarchies();
		}


		static class SupervisorExample implements TypedActor.Supervisor{
			
			public SupervisorStrategy supervisorStrategy() {
				return null;
			}

		}
		
		static class ReceiverExample implements TypedActor.Receiver {
	
			public void onReceive(Object MSG, ActorRef arg1) {
				
			}
		}

		public void squareDontCare(int i) {
			int sq = i * i ; 
		}
		public Future<Integer> square(int i) {
			return Futures.successful(i * i);
		}
		public Option<Integer> squareNowPlease(int i) {
			return Option.some(i * i);
		}
		public int squareNow(int i) {
			return i  * i;
		}
		
	}
	
	
	public void createATypedActor(){
		ActorSystem system = ActorSystem.create("akkaSystem");
		
		// Default Constructor
		Squarer mySquarer = TypedActor.get(system)
									   .typedActorOf(new TypedProps<SquarerImpl>(Squarer.class,SquarerImpl.class));
		
		
		// With Arugument constructor create object like this
		
		
	Squarer otherSquarer = TypedActor.get(system).typedActorOf(new TypedProps<SquarerImpl>(Squarer.class,new Creator<SquarerImpl>() {
			public SquarerImpl create() throws Exception {
				return new SquarerImpl("custom-constructor");
			}
		}), "name");
		
	mySquarer.squareDontCare(20);
	
	Option<Integer> oSquare = mySquarer.squareNowPlease(20);
	
	int iSquare = mySquarer.squareNow(20);
	
	Future<Integer> fSquare = mySquarer.square(20);
	
	// Stop Typed Actors
	
	TypedActor.get(system).stop(mySquarer);
	
	TypedActor.get(system).poisonPill(otherSquarer);
	
	}
	
	public static void proxyAnyActorRef(){
		ActorSystem system = ActorSystem.create("akka");
			
		ActorRef actorRefToRemoteActor = system.deadLetters();
		
		Squarer tySquarer = TypedActor.get(system).typedActorOf(new TypedProps<Squarer>(Squarer.class), actorRefToRemoteActor);
	}
	
	public static void createHierarchies(){
		Squarer childSquarer = TypedActor.get(TypedActor.context()).typedActorOf(new TypedProps<SquarerImpl>(Squarer.class,SquarerImpl.class));
		
		childSquarer.squareNow(10);
	}
	
	public void example(){
		createATypedActor();
		proxyAnyActorRef();
	}
	public static void main(String[] args) {
		TypedActorDocTest test = new TypedActorDocTest();
		test.example();
		
	}
}
