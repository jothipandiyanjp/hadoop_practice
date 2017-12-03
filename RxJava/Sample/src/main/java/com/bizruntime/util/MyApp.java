package com.bizruntime.util;

import java.util.Observable;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class MyApp{
	Logger log=Logger.getLogger(MyApp.class);
	public static void main(String[] args) {
		MyApp myapp=new MyApp();
		myapp.createEventSource();
		
	}
	private void createEventSource(){
		log.debug("Enter text --> ");
		EventSource eventSource = new EventSource();
        eventSource.addObserver((Observable obj, Object arg)->{
        	log.debug("Received response: " + arg);
        	
        });
      
        new Thread(eventSource).start();;
	}
}


class EventSource extends Observable implements Runnable{
	 public void run() {
	     System.out.println("run");   
		 while (true) {
	        	
	            String response = new Scanner(System.in).nextLine();
	            setChanged();
	            notifyObservers(response);
	        }
	    }
}
