package com.mahout.with.preference;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.apache.lucene.util.SloppyMath;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.hadoop.similarity.item.ItemSimilarityJob;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.CooccurrenceCountSimilarity;

public class MovieLensRecommenderApp {

	public static  final Logger LOG = Logger.getLogger(MovieLensRecommenderApp.class);

	public void recommendBook(){
		try {
			
			DataModel model = new FileDataModel(new File("src/main/resources/ml/ua.base"));
			
			UserSimilarity pearson = new PearsonCorrelationSimilarity(model);
			
			UserNeighborhood neighborhood = new NearestNUserNeighborhood(100, pearson, model);
//			UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, pearson, model);
			UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, pearson);			
			
			List<RecommendedItem> recommendations = recommender.recommend(10,50);

			for (RecommendedItem recommendation : recommendations) 
				  LOG.debug("recommendations for userID  : "+recommendation);
				
		} catch (IOException e) {
			LOG.error("File found execption occurred: "+ e.getMessage());
		}catch(NoSuchElementException e){
			LOG.error("Data in file were wrongly  misplaced : "+ e.getMessage());			
		}catch(TasteException e){
			LOG.error("Taste execption occurred : "+ e.getMessage());			
		}
		
	}
	
	public static void main(String[] args) {
		MovieLensRecommenderApp app = new MovieLensRecommenderApp();
		app.recommendBook();
	}
}
