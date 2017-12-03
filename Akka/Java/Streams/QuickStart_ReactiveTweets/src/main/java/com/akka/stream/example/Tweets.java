package com.akka.stream.example;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Tweets {
	
	public final String author;
	public final long timestamp;
	public final String body;
	
	public Tweets(String author, long timestamp, String body) {
		super();
		this.author = author;
		this.timestamp = timestamp;
		this.body = body;
	}
	
	

	public Set<String> hashtags() {
		Set<String> hashtags = Arrays.asList(body.split(" ")).stream().filter(t -> t.trim().startsWith("#"))
				.collect(Collectors.toSet());
		return hashtags;
	}
}
