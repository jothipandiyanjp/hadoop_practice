package com.akka.actors.helper;

public interface CounterServiceApi {

	public static final Object GetCurrentCount = "GetCurrentCount";
	
	public static class CurrentCount{
		public final String key;
		public final long count;
		public CurrentCount(String key, long count) {
			super();
			this.key = key;
			this.count = count;
		}
		
		@Override
		public String toString() {
			return String.format("%s(%s, %s)", getClass().getSimpleName(), key, count);
		}
	}
	
}
