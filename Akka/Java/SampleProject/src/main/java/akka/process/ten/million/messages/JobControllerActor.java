package akka.process.ten.million.messages;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class JobControllerActor extends UntypedActor {
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	int count = 0;
	long startedTime = System.currentTimeMillis();
	int no_of_msgs = 0;

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof String) {
			if (((String) message).compareTo("Done") == 0) {
				count++;
				if (count == no_of_msgs) {
					long now = System.currentTimeMillis();
					log.debug("All messages processed in "
							+ (now - startedTime) / 1000 + " seconds");

					log.debug("Total Number of messages processed "
							+ count);
					getContext().system().terminate();
				}
			}
		}

	}

	public JobControllerActor(int no_of_msgs) {
		this.no_of_msgs = no_of_msgs;
	}
}

