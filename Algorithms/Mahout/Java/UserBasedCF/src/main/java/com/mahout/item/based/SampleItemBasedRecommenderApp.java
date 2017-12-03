package com.mahout.item.based;

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
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class SampleItemBasedRecommenderApp {

	public static  final Logger LOG = Logger.getLogger(SampleItemBasedRecommenderApp.class);

	public void recommendBook(){
		try {
			
			DataModel model = new FileDataModel(new File("src/main/resources/Itembaseddataset.csv"));
			
			ItemSimilarity pearson = new PearsonCorrelationSimilarity(model);
			
			ItemBasedRecommender recommender = new GenericItemBasedRecommender(model, pearson);
						
				List<RecommendedItem> recommendations = recommender.recommend(5,10);
			
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
		SampleItemBasedRecommenderApp app = new SampleItemBasedRecommenderApp();
		app.recommendBook();
	}
}
