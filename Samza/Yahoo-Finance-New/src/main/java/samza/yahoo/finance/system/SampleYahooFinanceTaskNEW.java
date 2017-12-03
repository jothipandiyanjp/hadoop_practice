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

public class SampleYahooFinanceTaskNEW {

	private static Logger log = LoggerFactory
			.getLogger(YahooFinanceSystemFactory.class);

	private static HashMap<String, Double> originalData = new HashMap<String, Double>();
	private HashMap<String, Double> updatedByWindow = new HashMap<String, Double>();

	private static ObjectMapper mapper = new ObjectMapper();

	public static void process(URL url) {

		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String json = null;
			String jsonString="";
			while ((json = reader.readLine()) != null) {
				jsonString += json;
			}
			ObjectMapper obj=new ObjectMapper();
			
			HashMap<String, HashMap<String, ArrayList<Results>>> h=obj.readValue(jsonString, new TypeReference<HashMap<String,HashMap<String, Object>>>() {});
			System.out.println(h.get("list").get("resources").get(2));
		
		} catch (IOException e) {
			log.error(e.getMessage());
		}

	}

	public static void main(String[] args) {
		new Thread(SampleYahooFinanceTaskNEW::test).start();
		// new Thread(new SampleYahooFinanceTaskNEW()::window).start();
	}

	private static void test() {
		process(test1());

	}

	private static URL test1() {

		String baseURL = "http://finance.yahoo.com";
		String queryURL = "/webservice/v1/symbols";
		String companies = "/INFY.NS,INDORAMA.BO";
		String format = "/quote?format=json&view=detail";

		String fullUrlStr = null;
		fullUrlStr = baseURL + queryURL + companies + format;
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
