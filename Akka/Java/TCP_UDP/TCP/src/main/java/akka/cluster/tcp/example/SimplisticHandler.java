package akka.cluster.tcp.example;

import akka.actor.UntypedActor;
import akka.io.Tcp.ConnectionClosed;
import akka.io.Tcp.Received;
import akka.io.TcpMessage;
import akka.util.ByteString;

public class SimplisticHandler extends UntypedActor {
	  @Override
	  public void onReceive(Object msg) throws Exception {
	    if (msg instanceof Received) {
	      final ByteString data = ((Received) msg).data();
	      System.out.println(data);
	      getSender().tell(TcpMessage.write(data), getSelf());
	    } else if (msg instanceof ConnectionClosed) {
	      getContext().stop(getSelf());
	    }
	  }
	}