package com.mahout.data.model;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

public class PreferenceArrayApp {

	public void createPreferenceArray(){
		
		PreferenceArray prefsForUser1 = new GenericUserPreferenceArray(10);
		prefsForUser1.setUserID(0, 1l);
		prefsForUser1.setItemID(0, 101L);
		prefsForUser1.setValue(0, 3.0f);
		prefsForUser1.setItemID(1, 102L);
		prefsForUser1.setValue(1, 4.5f);
		
		FastByIDMap<PreferenceArray> preferences = new FastByIDMap<PreferenceArray>();
		preferences.put(1, prefsForUser1);
		
		DataModel model = new GenericDataModel(preferences);
		
	}
	
	public static void main(String[] args) {
		PreferenceArrayApp app =new PreferenceArrayApp();
		app.createPreferenceArray();
	}
}
