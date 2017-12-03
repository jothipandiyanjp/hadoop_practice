package samza.yahoo.finance.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.stream.Stream;

import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.util.BlockingEnvelopeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class YahooFinanceConsumer extends BlockingEnvelopeMap implements
		Runnable {
	private Logger log = LoggerFactory.getLogger(YahooFinanceConsumer.class);

	private String systemName;

	private SystemStreamPartition systemStreamPartition;

	private  String companies;
	
	public YahooFinanceConsumer() {
	}

	public YahooFinanceConsumer(String systemName, Config config,
			MetricsRegistry registry) {
		this.systemName = systemName;	
		this.companies=config.get("systems."+systemName+".companies");
	}

	public void run() {
		try {
			
			URL url = new URL("http://finance.yahoo.com/webservice/v1/symbols/"+this.companies+"/quote?format=json&view=detail");			
			BufferedReader reader=null;

			while (true) {
				try {
					reader = new BufferedReader(new InputStreamReader(url.openStream()));
					String json = null, jsonString="";

					while ((json = reader.readLine()) != null) jsonString += json;
					
					jsonString = jsonString.substring(jsonString.indexOf("["), jsonString.indexOf("]")+1);
					
				    	put(systemStreamPartition,new IncomingMessageEnvelope(systemStreamPartition,null, null,jsonString));			 

					Thread.sleep(2000);

				} catch (InterruptedException  | SocketException e) {
					log.error("Exception Occured "+e.getMessage());
					
				}catch ( IOException  e) {
					log.error("Exception Occured "+e.getMessage());	
				}
			}
		} catch (MalformedURLException e) {
			log.error("Exception Occured "+"Mention a valid URL..."+e.getMessage());	
			throw new RuntimeException();
		}
	}

	public void start() {
		new Thread(this).start();
	}

	public void stop() {
	log.debug("YahooFinanceConsumer stopped... ");
	}

	public void register(SystemStreamPartition systemStreamPartition,
			String offset) {
		this.systemStreamPartition=systemStreamPartition;		
		super.register(systemStreamPartition, offset);
	}
}
