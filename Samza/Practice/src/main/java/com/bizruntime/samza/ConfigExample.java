package com.bizruntime.samza;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.samza.config.Config;
import org.apache.samza.config.MapConfig;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;

public class ConfigExample implements StreamTask, InitableTask, WindowableTask {
	Logger log = Logger.getLogger("ConfigExample.class");

	@Override
	public void process(IncomingMessageEnvelope envelope,
			MessageCollector collector, TaskCoordinator coordinator)
			throws Exception {

	}

	@Override
	public void init(Config config, TaskContext context) throws Exception {

	}

	public static void main(String[] args) {

		ConfigExample conf = new ConfigExample();
		// conf.testConfig();
		HttpUriRequest uriRequest = new HttpGet(
				"http://api.openweathermap.org/data/2.5/weather?q=Bangalore&appid=8c1be8c6d66bab9a5e45a80a9b4c4b3a");

		try {
			URL url = new URL(
					"http://api.openweathermap.org/data/2.5/weather?q=Bangalore&appid=8c1be8c6d66bab9a5e45a80a9b4c4b3a");
			InputStream in = url.openStream();
            InputStreamReader isr = new InputStreamReader(in); 

            BufferedReader br = new BufferedReader(isr);
            String result = "";
            String read;
            while ((read = br.readLine()) != null)
            {
                result += read;
            }
            
            System.out.println(result);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {

		}

	}

	void testConfig() {
		Config config = new MapConfig();
		URI uri = null;
		try {
			uri = new URI("src/main/config/wikipedia-feed.properties");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void window(MessageCollector collector, TaskCoordinator coordinator)
			throws Exception {

	}
}
