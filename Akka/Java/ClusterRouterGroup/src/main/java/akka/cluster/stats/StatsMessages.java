package akka.cluster.stats;

import java.io.Serializable;

public interface StatsMessages {

	public static class StatsJob implements Serializable {
		private final String text;

		public StatsJob(String text) {
			super();
			this.text = text;
		}

		public String getText() {
			return text;
		}

		@Override
		public String toString() {
			return "StatsJob [text=" + text + "]";
		}
		
	}

	public static class StatsResult implements Serializable {
		private final double meanWordLength;

		public StatsResult(double meanWordLength) {
			super();
			this.meanWordLength = meanWordLength;
		}

		public double getMeanWordLength() {
			return meanWordLength;
		}

		@Override
		public String toString() {
			return "StatsResult [meanWordLength=" + meanWordLength + "]";
		}
		
	}

	public static class JobFailed implements Serializable {
		private final String reason;

		public JobFailed(String reason) {
			super();
			this.reason = reason;
		}

		public String getReason() {
			return reason;
		}

		@Override
		public String toString() {
			return "JobFailed [reason=" + reason + "]";
		}
		
	}

}
