package akka.cluster.worker;

import scala.concurrent.duration.Deadline;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.persistence.UntypedPersistentActor;

public class Master extends UntypedPersistentActor {

	private static abstract class WorkerStatus {
		protected abstract boolean isIdle();

		private boolean isBusy() {
			return !isIdle();
		};

		protected abstract String getWorkId();

		protected abstract Deadline getDeadLine();
	}

	private static final class Idle extends WorkerStatus {
		private static final Idle instance = new Idle();

		@Override
		protected Deadline getDeadLine() {
			throw new IllegalAccessError();
		}

		@Override
		protected String getWorkId() {
			throw new IllegalAccessError();
		}

		@Override
		protected boolean isIdle() {
			return true;
		}

		protected Idle getIdle() {
			return instance;
		}

		@Override
		public String toString() {
			return "Idle";
		}
	}

	private static final class Busy extends WorkerStatus {
		private final String workId;
		private final Deadline deadline;

		private Busy(String workId, Deadline deadline) {
			this.workId = workId;
			this.deadline = deadline;
		}

		@Override
		protected boolean isIdle() {
			return false;
		}

		@Override
		protected String getWorkId() {
			return workId;
		}

		@Override
		protected Deadline getDeadLine() {
			return deadline;
		}

		@Override
		public String toString() {
			return "Busy{" + "work=" + workId + ", deadline=" + deadline + '}';
		}
	}

	private static final class WorkerState {
		public final ActorRef ref;
		public final WorkerStatus status;

		private WorkerState(ActorRef ref, WorkerStatus status) {
			this.ref = ref;
			this.status = status;
		}

		private WorkerState copyWithRef(ActorRef ref) {
			return new WorkerState(ref, this.status);
		}

		private WorkerState copyWithStatus(WorkerStatus status) {
			return new WorkerState(this.ref, status);
		}
		
		
	}

	
	@Override
	public void onReceiveCommand(Object arg0) throws Exception {
		
	}
	
	@Override
	public void onReceiveRecover(Object arg0) throws Exception {
		
	}

	public String persistenceId() {
		// TODO Auto-generated method stub
		return null;
	}
}
