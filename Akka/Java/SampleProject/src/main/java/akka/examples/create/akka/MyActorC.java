package akka.examples.create.akka;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;

public class MyActorC implements Creator<MyActor> {

	public MyActor create() throws Exception {
		return new MyActor();
	}

	void doSomeOperaitions() {
		Props props1 = Props.create(MyUnTypedActor.class);
		// Props props2 = Props.create(MyActorC1.class,"...");
		Props props3 = Props.create(new MyActorC()); // Top-Level
		Props props4 = Props.create(new MyActorC()); // Static
		Props props5 = Props.create(ParametricCreator.class,"...");  // parameterized construcor

	}

	public static void main(String[] args) {
		MyActorC example = new MyActorC();
		example.doSomeOperaitions();
	}

	static class MyActorC1 implements Creator<MyActor> {   //  No parameter
		public MyActor create() throws Exception {
			return new MyActor();
		}
	}
	
	static class ParametricCreator<T extends MyActor> implements Creator<T> {  // Parameter class
		
		public T create() throws Exception {
			return  null;
		}
	}
	
}

class MyActor extends UntypedActor {
	@Override
	public void onReceive(Object arg0) throws Exception {
	}
}