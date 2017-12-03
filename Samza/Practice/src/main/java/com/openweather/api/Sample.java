package com.openweather.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;

public class Sample {
	public static void main(String[] args) throws UnknownHostException,
			IOException {

		String postdata = "appid" + "="
				+ URLEncoder.encode("YahooDemo", "UTF-8");

		postdata += "&" + "query" + "="
				+ URLEncoder.encode("umbrella", "UTF-8");

		postdata += "&" + "results" + "=" + URLEncoder.encode("10", "UTF-8");

		String hostname = "finance.yahoo.com";

		int port = 80;

		InetAddress addr = InetAddress.getByName(hostname);

		Socket socket = new Socket(addr, port);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream(), "UTF8"));
		String path = "webservice/v1/symbols/COALINDIA.NS/quote?format=json&view=detail";
		bw.write("POST " + path + " HTTP/1.0\r\n");
		
		 bw.write("Content-Length: " + postdata.length() + "\r\n");

		    bw.write("Content-Type: application/x-www-form-urlencoded\r\n");

		    bw.write("\r\n"); 
		// Send POST data string
		    bw.write(postdata);

		    bw.flush();

		    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            
            bw.close();
            br.close();
		
	}
}
