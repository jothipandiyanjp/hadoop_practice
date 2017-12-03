package com.mahout.examples;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;


public class SVDWithALSApp {

	private static final Logger LOGGER = Logger.getLogger(SVDWithALSApp.class);
	
	public void recommend(){
		try {
			DataModel svdModel = new FileDataModel(new File("src/main/resources/movies"));
		
			int numFeatures = 3;
			double lambda = 0.065;
			int numIterations = 1;
			ALSWRFactorizer factorizer = new ALSWRFactorizer(svdModel, numFeatures, lambda, numIterations);
		
			Recommender recommender  = new SVDRecommender(svdModel, factorizer);
			
			for (RecommendedItem recommendation :recommender.recommend(3,1))
			{
			System.out.println(recommendation);
			}
			
		} catch (IOException e) {
			LOGGER.error("IO exception occurred "+e.getMessage());
		} catch (TasteException e) {
			LOGGER.error("TasteException occurred "+e.getMessage());
		}
		
	}
	public static void main(String[] args) {
		SVDWithALSApp app =new SVDWithALSApp();
		app.recommend();
		
	}
}
