package akka.cluster.tcp.example;

import java.net.InetSocketAddress;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.io.Tcp.ConnectionClosed;
import akka.io.Tcp.Received;
import akka.io.TcpMessage;
import akka.japi.Procedure;
import akka.util.ByteString;
import akka.util.ByteString.ByteStrings;

public class Client extends UntypedActor {

	final InetSocketAddress remote;
	final ActorRef listener;
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public Client(InetSocketAddress remote,ActorRef listner) {
		this.remote = remote;
		this.listener = listner;
		
	    final ActorRef tcp = Tcp.get(getContext().system()).manager();
	    tcp.tell(TcpMessage.connect(remote), getSelf());   
	}



	public void onReceive(Object message) throws Exception {
		if (message instanceof CommandFailed) {
	        listener.tell("failed", getSelf());
	        getContext().stop(getSelf());
	    }else if (message instanceof Connected) {
	    	log.debug("connected");
	        listener.tell(message, getSelf());
	        log.debug("sending message from client to server "+message);
	        getSender().tell(TcpMessage.register(getSelf()), getSelf());
	        getContext().become(connected(getSender()));
	    }
	}

	private Procedure<Object> connected(final ActorRef connection) {
		  return new Procedure<Object>() {
			  @Override
			public void apply(Object message) throws Exception {
			        if (message instanceof ByteStrings) {
			            connection.tell(TcpMessage.write((ByteString) message), getSelf());
			        }else if( message instanceof CommandFailed){
			        	
			        } else if (message instanceof Received) {
			            listener.tell(((Received) message).data(), getSelf());
			        } else if (message.equals("close")) {
			            connection.tell(TcpMessage.close(), getSelf());
			        } else if (message instanceof ConnectionClosed) {
			            getContext().stop(getSelf());
			        }
			  }
			 };
	}
}
