package com.mahout.mahout.essentials;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.mahout.clustering.conversion.InputDriver;
import org.apache.mahout.clustering.display.DisplayKMeans;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.text.SequenceFilesFromDirectory;
import org.apache.mahout.utils.clustering.ClusterDumper;

public class KMeansApp {

	private static final Logger LOGGER  = Logger.getLogger(KMeansApp.class);

	private static final String DIRECTORY_CONTAINING_CONVERTED_INPUT =
			"output";
	
	
	private void run(Configuration conf, Path input, Path output,
			EuclideanDistanceMeasure measure, int k, double convergenceDelta,
			int maxIterations) {

		Path directoryContainingConvertedInput = new Path(output,
				DIRECTORY_CONTAINING_CONVERTED_INPUT);
		try {
		
			// Input should be given as sequence file format
			InputDriver.runJob(input, directoryContainingConvertedInput,
					"org.apache.mahout.math.RandomAccessSparseVector");
			SequenceFilesFromDirectory.main(new String[]{"src/main/resources/essentials/input/kmeansData","src/main/resources/essentials/input/k"});
			
			/*// Get initial clusters randomly
			Path clusters = new Path(output, "random-seeds");
			clusters = RandomSeedGenerator.buildRandom(conf,
					directoryContainingConvertedInput, clusters, k, measure);
			
			KMeansDriver.run(conf, directoryContainingConvertedInput,
					clusters, output, convergenceDelta,
					10, true, 0.0, false);
		
			// run ClusterDumper to display result
			Path outGlob = new Path(output, "clusters-*-final");
			Path clusteredPoints = new Path(output,"clusteredPoints");
			
			ClusterDumper clusterDumper = new ClusterDumper(outGlob,clusteredPoints);
			clusterDumper.printClusters(null);
			*/
	
		} catch (ClassNotFoundException e) {
			LOGGER.error("ClassNotfound exception occurred : "+e.getMessage());
		} catch (IOException e) {
			LOGGER.error("IOException occurred : "+e.getMessage());
		} catch (InterruptedException e) {
			LOGGER.error("InterruptedException occurred : "+e.getMessage());
		} catch (Exception e) {
			LOGGER.error("exception occurred : "+e.getMessage());
		}
		
	}
	public void clusteringByHeightAndWeight(){
		Path output = new Path("src/main/resources/essentials/output");
		Configuration conf = new Configuration();

		try {
			HadoopUtil.delete(conf, output);
			run(conf, new Path("src/main/resources/essentials/input/kmeansData"), output,
					new EuclideanDistanceMeasure(), 2, 0.5, 10);
			
		} catch (IOException e) {
			LOGGER.error("IOException occurred : "+e.getMessage());
		}
		
	}
	
	

	public static void main(String[] args) {
		KMeansApp app = new KMeansApp();
		app.clusteringByHeightAndWeight();
		
	}
}
