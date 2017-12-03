package samza.yahoo.finance.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.util.BlockingEnvelopeMap;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samza.yahoo.finance.pojo.Results;
import samza.yahoo.finance.pojo.YahooFinance;



public class SampleYahooFinanceConsumer extends BlockingEnvelopeMap {
	private Logger log = LoggerFactory.getLogger(SampleYahooFinanceConsumer.class);

	private String systemName;
	
	private String baseURL;
	private String queryURL;
	private String companies;
	private String format;
	  private Map<String, Double> allData = new HashMap<String, Double>();

	private SystemStreamPartition systemStreamPartition;

	public SampleYahooFinanceConsumer() {
	
	}
	
	public SampleYahooFinanceConsumer(String systemName, Config config, MetricsRegistry registry) {

		this.systemName = systemName;
		/*url.setBaseURL(config.get("systems."+systemName+".baseURL"));
		url.setQueryURL(config.get("systems."+systemName+".queryURL"));
		url.setCompanies(config.get("systems."+systemName+".companies"));
		url.setFormat(config.get("systems."+systemName+".format"));*/

		this.baseURL=config.get("systems."+systemName+".baseURL");
		this.queryURL=config.get("systems."+systemName+".queryURL");
		this.companies=config.get("systems."+systemName+".companies");
		this.format=config.get("systems."+systemName+".format");

	}

	public String getMessages() throws IOException {
		ObjectMapper mapper=new ObjectMapper();
		ArrayList<YahooFinance> list=null;
		BufferedReader reader=null;

			String fullUrlStr = this.baseURL + URLEncoder.encode(this.queryURL+this.companies, "UTF-8")+this.format;
			URL fullUrl = new URL(fullUrlStr);
			System.out.println(fullUrlStr);

			while (true) {
				try {
					reader=new BufferedReader(new InputStreamReader(fullUrl.openStream()));

			/*		put(systemStreamPartition,new IncomingMessageEnvelope(systemStreamPartition,
									null, null,	reader.readLine()));
			 */
					HashMap<String,Results> map=mapper.readValue(reader.readLine(),new TypeReference<HashMap<String, Results>>() {});
					list=map.get("query")
							.getResults()
							.get("quote");
					
					list.parallelStream()
						.forEach(yf-> allData.put(yf.getSymbol(),Double.parseDouble(yf.getChange())));
					
					log.debug(""+allData);
					allData.entrySet().forEach(System.out::println);;
					Thread.sleep(1000);
					}catch ( IOException  e) {
					log.error(e.getMessage());	
				} catch (InterruptedException e) {
						e.printStackTrace();
					}
		
			}
		
	}

	
	public void start() {
//	  new Thread(new SampleYahooFinanceConsumer()::getMessages).start();
	}
	
	public void stop() {
	}

	public void register(SystemStreamPartition systemStreamPartition,
			String offset) {
		this.systemStreamPartition = systemStreamPartition;
		super.register(systemStreamPartition, offset);
	}

	public static void main(String[] args) throws IOException {
		SampleYahooFinanceConsumer yh=new SampleYahooFinanceConsumer();
		yh.test();
	}
	public   void test() throws IOException{

		SampleYahooFinanceConsumer y=new SampleYahooFinanceConsumer();
		Properties prop=new Properties();
		try {
			prop.load(SampleYahooFinanceConsumer.class.getClassLoader().getResourceAsStream("yahoo-finance-feed.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		y.baseURL=prop.getProperty("systems.yahoo-finance.baseURL");
		y.queryURL=prop.getProperty("systems.yahoo-finance.queryURL");
		y.companies=prop.getProperty("systems.yahoo-finance.companies");
		y.format=prop.getProperty("systems.yahoo-finance.format");
		

	}
}
