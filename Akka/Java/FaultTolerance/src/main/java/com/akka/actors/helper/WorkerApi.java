package com.akka.actors.helper;

public interface WorkerApi {

	public static final Object Start ="Start";
	public static final Object Do ="Do";
	
	public static class Progress{
		public final double percent ;

		public Progress(double percent) {
			super();
			this.percent = percent;
		}

		@Override
		public String toString() {
			return String.format("%s(%s)", getClass().getSimpleName(),percent);
		}
		
		
	}
}
