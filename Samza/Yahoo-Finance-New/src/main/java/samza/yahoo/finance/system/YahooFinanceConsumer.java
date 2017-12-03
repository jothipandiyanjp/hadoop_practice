package samza.yahoo.finance.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.util.BlockingEnvelopeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class YahooFinanceConsumer extends BlockingEnvelopeMap implements Runnable{
	private Logger log = LoggerFactory.getLogger(YahooFinanceConsumer.class);

	private String systemName;	
	
	private String BASEURL="http://query.yahooapis.com/v1/public/yql?q=";
	private String QUERY="select symbol,Change,DaysLow,DaysHigh,Name,StockExchange,YearRange from yahoo.finance.quotes where symbol in ";
	private String companies;
	private String FORMAT="&format=json&env=store://datatables.org/alltableswithkeys";

	private SystemStreamPartition systemStreamPartition;

	public YahooFinanceConsumer() {
	
	}
	
	public YahooFinanceConsumer(String systemName, Config config, MetricsRegistry registry) {
		this.systemName = systemName;	
		this.companies=config.get("systems."+systemName+".companies");
	}

	public  void run() {

		try {

			// Collecting url
			String fullUrlStr = this.BASEURL + URLEncoder.encode(this.QUERY+this.companies, "UTF-8")+this.FORMAT;
			URL fullUrl = new URL(fullUrlStr);
			BufferedReader reader=null;
			log.debug("-->"+fullUrlStr);
			while (true) {
				try {
					reader=new BufferedReader(new InputStreamReader(fullUrl.openStream()));

					
					put(systemStreamPartition,new IncomingMessageEnvelope(systemStreamPartition,
									null, null,	reader.readLine()));			 

					Thread.sleep(1000);

				} catch (InterruptedException  | SocketException e) {
					log.error(e.getMessage());	
				}catch ( IOException  e) {
					log.error(e.getMessage());	
				}
			}
		} catch (MalformedURLException | UnsupportedEncodingException e) {
			log.error("Mention a valid URL..."+e.getMessage());	
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
		this.systemStreamPartition = systemStreamPartition;
		super.register(systemStreamPartition, offset);
	}
}
