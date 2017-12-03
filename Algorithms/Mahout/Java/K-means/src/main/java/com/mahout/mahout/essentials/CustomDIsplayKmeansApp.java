package com.mahout.mahout.essentials;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.classify.ClusterClassifier;
import org.apache.mahout.clustering.conversion.InputDriver;
import org.apache.mahout.clustering.display.DisplayClustering;
import org.apache.mahout.clustering.display.DisplayKMeans;
import org.apache.mahout.clustering.iterator.ClusterIterator;
import org.apache.mahout.clustering.iterator.KMeansClusteringPolicy;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.Vector;
import org.apache.mahout.utils.clustering.ClusterDumper;

import com.google.common.collect.Lists;

public class CustomDIsplayKmeansApp  extends DisplayClustering{

	private static final Logger LOGGER  = Logger.getLogger(KMeansApp.class);

	private static final String DIRECTORY_CONTAINING_CONVERTED_INPUT =
			"output";
	
	
	public CustomDIsplayKmeansApp() {
		initialize();
	}
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
	    Path samples = new Path("src/main/resources/essentials/input/kmeansData");
	    Path output = new Path("output");
	    Configuration conf = new Configuration();
	    HadoopUtil.delete(conf, output);
	    Path directoryContainingConvertedInput = new Path(output,
				DIRECTORY_CONTAINING_CONVERTED_INPUT);
		InputDriver.runJob(samples, directoryContainingConvertedInput,
				"org.apache.mahout.math.RandomAccessSparseVector");

		  generateSamples();

		boolean runClusterer = true;
	    double convergenceDelta = 0.001;
	    int numClusters = 3;
	    int maxIterations = 10;
	    if (runClusterer) {
	//      runSequentialKMeansClusterer(conf, samples, output, new EuclideanDistanceMeasure(), numClusters,  convergenceDelta,maxIterations);
	    } else {
//	      runSequentialKMeansClassifier(conf, samples, output, new EuclideanDistanceMeasure(), numClusters, maxIterations, convergenceDelta);
	    }
	    new CustomDIsplayKmeansApp();

	}
	
	private static void runSequentialKMeansClusterer(Configuration conf, Path samples, Path output,
			EuclideanDistanceMeasure measure, int numClusters, double convergenceDelta,
			int maxIterations) throws IOException {

		Collection<Vector> points = Lists.newArrayList();
	    for (int i = 0; i < numClusters; i++) {
	      points.add(SAMPLE_DATA.get(i).get());
	    }
	    List<Cluster> initialClusters = Lists.newArrayList();
	    int id = 0;
	    for (Vector point : points) {
	      initialClusters.add(new org.apache.mahout.clustering.kmeans.Kluster(point, id++, measure));
	    }
	    ClusterClassifier prior = new ClusterClassifier(initialClusters, new KMeansClusteringPolicy(convergenceDelta));
	    Path priorPath = new Path(output, Cluster.INITIAL_CLUSTERS_DIR);
	    prior.writeToSeqFiles(priorPath);
	    
	    ClusterIterator.iterateSeq(conf, samples, priorPath, output, maxIterations);
	    loadClustersWritable(output);
		
	}
	
}
