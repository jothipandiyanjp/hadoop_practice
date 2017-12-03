package akka.extension.example;

import java.sql.Connection;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class ExtensionApp 
{
	
	public void extensionExample(){
		ActorSystem system = ActorSystem.create("AkkaSystem");
		ActorRef actor = system.actorOf(Props.create(MyActor.class), "MyActor");
	}
    public static void main( String[] args )
    {
    	ExtensionApp app =new ExtensionApp();
    	app.extensionExample();
    }
}

class MyActor extends UntypedActor {

	@Override
	public void preStart() throws Exception {
		  final SettingsImpl settings = Settings.SettingsProvider.get(getContext().system());
		  Connection connection = connect(settings.DB_URI, settings.CIRCUIT_BREAKER_TIMEOUT);
	}
	
	private Connection connect(String dB_URI, Duration cIRCUIT_BREAKER_TIMEOUT) {
		System.out.println(dB_URI);
		System.out.println(cIRCUIT_BREAKER_TIMEOUT);
		return null;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		
	}
}