package com.bizruntime.yarn;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
//import org.apache.hadoop.yarn.client.api.YarnClient;
//import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;

public class Client {
public static void main(String[] args) throws YarnException, IOException {
	Client client=new Client();
	client.createYarnClient();
}


/*private void createYarnClient() {
	
    // Initialize clients to ResourceManager and NodeManagers
	Configuration config=new YarnConfiguration();
  //  YarnClient yarnClient = YarnClient.createYarnClient();
	//yarnClient.init(config);
	//yarnClient.start();
	
	try {
	YarnClientApplication app=yarnClient.createApplication();
		GetNewApplicationResponse appResponse=app.getNewApplicationResponse();
		
		
	} catch (YarnException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	
	
}*/
private void createYarnClient() throws YarnException, IOException {
	Configuration config=new YarnConfiguration();
	 
	YarnClient yarnClient = YarnClient.createYarnClient();
	  yarnClient.init(config);
	  yarnClient.start();
	  
	  YarnClientApplication app = yarnClient.createApplication();
	  GetNewApplicationResponse appResponse = app.getNewApplicationResponse();
	  
	  ApplicationSubmissionContext appContext = app.getApplicationSubmissionContext();
	  ApplicationId appId = appContext.getApplicationId();
	  
	  

//	  appContext.setKeepContainersAcrossApplicationAttempts("");
	  appContext.setApplicationName("");

	  
}

}
