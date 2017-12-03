package akka.extension.example;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;

import com.typesafe.config.Config;

import akka.actor.Extension;

public class SettingsImpl  implements Extension{

	public final String DB_URI;
    public final Duration CIRCUIT_BREAKER_TIMEOUT;

	  
	public SettingsImpl(Config config) {
	    DB_URI = config.getString("myapp.db.uri");
	    CIRCUIT_BREAKER_TIMEOUT = Duration.create(config.getDuration("myapp.circuit-breaker.timeout",
	    							TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
	}
	
	
}
