package samza.yahoo.finance.system;


import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samza.yahoo.finance.pojo.Results;
import samza.yahoo.finance.pojo.YahooFinance;
import samza.yahoo.finance.system.SampleYahooFinanceConsumer;
import samza.yahoo.finance.system.YahooFinanceSystemFactory;
import samza.yahoo.finance.alert.SendMail;

public class SampleYahooFinanceTask{
	
	  private static Logger log=LoggerFactory.getLogger(YahooFinanceSystemFactory.class);

	  private static HashMap<String, Double> originalData = new HashMap<String, Double>();
	  private HashMap<String, Double> updatedByWindow = new HashMap<String, Double>();
	  
	  private static ObjectMapper mapper=new ObjectMapper();
	  

	public static void process(URL url) {
			
		BufferedReader reader=null;

		while (true) {
			try {
				reader=new BufferedReader(new InputStreamReader(url.openStream()));
				HashMap<String,Results> map=mapper.readValue(reader.readLine(),new TypeReference<HashMap<String, Results>>() {});
				ArrayList<YahooFinance> list=map.get("query")
												.getResults()
												.get("quote");			
				list.parallelStream()
				.forEach(stockDetailByCompany-> originalData.put(stockDetailByCompany.getSymbol(), Double.parseDouble(stockDetailByCompany.getChange())));
				
				Thread.sleep(1000);
				}catch ( IOException  e) {
				log.error(e.getMessage());	
			} catch (InterruptedException e) {
					e.printStackTrace();
				}
	
		}
		

	}
	
	public void window() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while(true){		
		if(updatedByWindow. isEmpty()){
			originalData.entrySet()
				   .parallelStream()
				   .forEach((symbol)->updatedByWindow.put(symbol.getKey(),symbol.getValue()));			
		}

		updatedByWindow.entrySet()
		.stream()
		.filter((symbol)->{ System.out.println("symbol --> "+symbol+"key --> "+symbol.getKey()+" value --> "+symbol.getValue()+"original data"+originalData.get(symbol.getKey())+"uppdated"+symbol.getValue()+"boolean "+ (originalData.get(symbol.getKey()).doubleValue()!=symbol.getValue().doubleValue()));return originalData.get(symbol.getKey()).doubleValue()!=(symbol.getValue()).doubleValue();})
		.forEach((symbol)->{
			System.out.println("foreach symbol "+symbol);			
			new SendMail ().createAlert(symbol.getKey(),originalData.get(symbol.getKey()));
			updatedByWindow.put(symbol.getKey(),originalData.get(symbol.getKey()));
			});		
		}
	}
	public static void main(String[] args) {
		new Thread(SampleYahooFinanceTask::test).start();
		new Thread(new SampleYahooFinanceTask()::window).start();		
	}

	private static void test() {
		process(test1());
		
	}
	private static URL test1() {
		
		Properties prop=new Properties();
		try {
			prop.load(SampleYahooFinanceConsumer.class.getClassLoader().getResourceAsStream("yahoo-finance-feed.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String baseURL=prop.getProperty("systems.yahoo-finance.baseURL");
		String queryURL=prop.getProperty("systems.yahoo-finance.queryURL");
		String companies=prop.getProperty("systems.yahoo-finance.companies");
		String format=prop.getProperty("systems.yahoo-finance.format");		
	
			String fullUrlStr = null;
			try {
				fullUrlStr = baseURL + URLEncoder.encode(queryURL+companies, "UTF-8")+format;
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			URL fullUrl = null;
			try {
				fullUrl = new URL(fullUrlStr);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			System.out.println(fullUrlStr);
			
			return fullUrl;
				
	}
}
