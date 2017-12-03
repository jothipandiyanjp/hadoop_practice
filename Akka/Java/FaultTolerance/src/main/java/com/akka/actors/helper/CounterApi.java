package com.akka.actors.helper;

import akka.actor.ActorRef;

public class CounterApi {

	public static class UseStorage{
		
		public final ActorRef storage;

		public UseStorage(ActorRef storage) {
			super();
			this.storage = storage;
		}
		
		 public String toString() {
		        return String.format("%s(%s)", getClass().getSimpleName(), storage);
		      }

	}
}
