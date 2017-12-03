	package com.mahout.with.preference;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.apache.lucene.util.SloppyMath;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class SampleRecommenderApp {

	public static  final Logger LOG = Logger.getLogger(SampleRecommenderApp.class);

	public void recommendBook(){
		try {
			
			DataModel model = new FileDataModel(new File("src/main/resources/dataset.csv"));
			
			UserSimilarity pearson = new PearsonCorrelationSimilarity(model);
			
			LOG.debug("Pearson similarity : "+pearson.userSimilarity(1, 2));
			LOG.debug("Pearson similarity : "+pearson.userSimilarity(1, 5));
			LOG.debug("Pearson similarity : "+pearson.userSimilarity(1, 4));
			
			UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, pearson, model);
			int i=1;
			while(i<=5){
				for(long neighbors : neighborhood.getUserNeighborhood(i) )
					LOG.debug("user id :"+(i)+ " and his/her neighbors "+neighbors);
				i++;
			}
			// creating recommender engine
			UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, pearson);
			
			// userid -> 1 
			// no of recommendations -> 1
			List<RecommendedItem> recommendations = recommender.recommend(1,10);

			for (RecommendedItem recommendation : recommendations) 
				  LOG.debug("recommendations for userID 1 : "+recommendation);
				
		} catch (IOException e) {
			LOG.error("File found execption occurred: "+ e.getMessage());
		}catch(NoSuchElementException e){
			LOG.error("Data in file were wrongly  misplaced : "+ e.getMessage());			
		}catch(TasteException e){
			LOG.error("Taste execption occurred : "+ e.getMessage());			
		}		
	}
	
	public static void main(String[] args) {
		SampleRecommenderApp app = new SampleRecommenderApp();
		app.recommendBook();
	}
}
