package akka.transform.message;

import java.io.Serializable;

public class TransformationMessages {
	
	public static class TransformationJob implements Serializable{
		private String text;

		public TransformationJob(String text) {
			super();
			this.text = text;
		}

		public String getText() {
			return text;
		}

	}
	
	public static class TransformataionResult implements Serializable{
		private String text;

		public TransformataionResult(String text) {
			super();
			this.text = text;
		}

		public String getText() {
			return text;
		}

		@Override
		public String toString() {
			return "TransformataionResult [text=" + text + "]";
		}

	}
	
	public static class JobFailed implements Serializable{
		private final String reason;
		private final TransformationJob job;
		public JobFailed(String reason, TransformationJob job) {
			super();
			this.reason = reason;
			this.job = job;
		}
		public String getReason() {
			return reason;
		}
		public TransformationJob getJob() {
			return job;
		}
		@Override
		public String toString() {
			return "JobFailed [reason=" + reason + ", job=" + job + "]";
		}
		
	}
	public static final String BACKEND_REGISTRATION = "BackendRegistration";
}
