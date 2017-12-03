package com.openweather.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestAPIExample {
	Logger log=LoggerFactory.getLogger(RequestAPIExample.class);
	public static void main(String[] args) throws IOException {
		RequestAPIExample example=new  RequestAPIExample();
		example.testStockPrice();
 
    }
	private  void testStockPrice() throws IOException {
		String url = "http://query.yahooapis.com/v1/yql/";
        URL YahooURL = null;
		try {
			YahooURL = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
        while(true){
        HttpURLConnection con  = (HttpURLConnection) YahooURL.openConnection();
        con.setRequestMethod("GET");
        String inputLine;
        StringBuilder response = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        log.debug(response.toString());
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
       }
	}
}
