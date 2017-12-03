package com.akka.actors.helper;

public class Increment {

	public final long n;

	public Increment(long n) {
		super();
		this.n = n;
	}

	
	@Override
	public String toString() {
		return String.format("%s(%s)", getClass().getSimpleName(), n);
	}
	
	
}
