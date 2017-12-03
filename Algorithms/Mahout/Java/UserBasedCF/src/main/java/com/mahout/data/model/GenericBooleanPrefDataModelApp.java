package com.mahout.data.model;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.util.SloppyMath;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.distance.MahalanobisDistanceMeasure;


public class GenericBooleanPrefDataModelApp {
	private static final Logger LOG = Logger.getLogger(GenericBooleanPrefDataModelApp.class);
	public void createDataModel() {

		try {

			DataModel model = new GenericBooleanPrefDataModel(
					GenericBooleanPrefDataModel.toDataMap(new FileDataModel(
							new File("src/main/resources/ml/ua.base"))));

			RecommenderIRStatsEvaluator evaluator =
					 new GenericRecommenderIRStatsEvaluator ();
			
			RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {			
				@Override
				public Recommender buildRecommender(DataModel dataModel)
						throws TasteException {
					UserSimilarity similarity = new TanimotoCoefficientSimilarity(
							model);	
					UserNeighborhood neighborhood = new NearestNUserNeighborhood(
							100,similarity, model);
					return new GenericUserBasedRecommender(model,
							neighborhood, similarity);
				}
			};
						
			DataModelBuilder modelBuilder  = new  DataModelBuilder() {		
				@Override
				public DataModel buildDataModel(FastByIDMap<PreferenceArray> trainingData) {
					
					return new GenericBooleanPrefDataModel(
							GenericBooleanPrefDataModel.toDataMap(
									trainingData));
				}
			};
			
			IRStatistics score =	evaluator.evaluate(recommenderBuilder, modelBuilder, model, null, 5, 
					GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);			
			
			LOG.debug("Precision -> "+score.getPrecision());
			LOG.debug("Recall -> "+score.getRecall());
			
		} catch (TasteException | IOException e) {
			e.printStackTrace();
		}
		LOG.debug("method ends here..");

	}

	public static void main(String[] args) {
		GenericBooleanPrefDataModelApp app = new GenericBooleanPrefDataModelApp();
		app.createDataModel();
	}
}
