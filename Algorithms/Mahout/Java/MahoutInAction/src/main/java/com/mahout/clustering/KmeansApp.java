package com.mahout.clustering;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Reader;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.SequenceFile.Writer.Option;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
import org.apache.mahout.clustering.classify.WeightedVectorWritable;
import org.apache.mahout.clustering.iterator.ClusterWritable;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.Kluster;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.clustering.topdown.PathDirectory;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

public class KmeansApp {
	private static final Logger LOGGER = Logger.getLogger(KmeansApp.class);
	private static final double[][] points = new double[][] { { 1, 1 },
			{ 2, 1 }, { 1, 2 }, { 2, 2 }, { 3, 3 }, { 8, 8 }, { 8, 9 },
			{ 9, 8 }, { 9, 9 } };

	private void writePointsToFile(List<Vector> points, String fileName,
			FileSystem fs, Configuration conf) {

		Path filepath = new Path(fileName);
		try {

			/*
			 * Deprecated 
			 * SequenceFile.Writer(fs, conf, path,
			 * LongWritable.class, VectorWritable.class);
			 */

			Option keyClass = Writer.keyClass(LongWritable.class);
			Option valClass = Writer.valueClass(VectorWritable.class);
			Option path = Writer.file(filepath);

			SequenceFile.Writer writer = SequenceFile.createWriter(conf,
					keyClass, valClass, path);

			long recNum = 0;
			VectorWritable vec = new VectorWritable();

			for (Vector point : points) {
				vec.set(point);
				writer.append(new LongWritable(recNum++), vec);
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public List<Vector> getPoints(double[][] raw) {
		List<Vector> points = new ArrayList<Vector>();

		for (double[] fr : raw) {
			// creating vector for given points
			Vector vec = new RandomAccessSparseVector(fr.length);
			vec.assign(fr);
			points.add(vec);
		}
		return points;
	}

	public void createCluster() {

		// converting two dimensional double to vector
		List<Vector> vectors = getPoints(points);

		Predicate<File> predicate = (File file) -> !file.exists() ? file
				.mkdir() : true;

		File testData = new File("testData");
		predicate.test(testData); // if file is not present it will create

		testData = new File("testData/points");
		predicate.test(testData);

		Configuration conf = new Configuration();
		try {
			FileSystem fs = FileSystem.get(conf);

			writePointsToFile(vectors, "testData/points/file1", fs, conf);
			
			
			Path path  = new Path("testData/clusters/part-00000");
			
			Option keyClass = Writer.keyClass(Text.class);
			Option valClass = Writer.valueClass(Kluster.class);
			Option opath = Writer.file(path);
			
			SequenceFile.Writer writer = SequenceFile.createWriter(conf,
					keyClass, valClass, opath);
			int k=2;
			for(int i=0;i<k;i++){
				Vector vec = vectors.get(i);
				Kluster cluster  = new Kluster(vec, i, new EuclideanDistanceMeasure());
				writer.append(new Text(cluster.getIdentifier()), cluster);
			}
			writer.close();
			
			Path input = new Path("testData/points");
			Path clustersIn = new Path("testData/clusters");
			Path output = new Path("output");	
			
			double convergenceDelta = 0.001;
			int maxIterations = 10;
			boolean runClustering = true;
			double clusterClassificationThreshold=0;
			boolean runSequential=false;
			
			KMeansDriver.run(conf, input, clustersIn, 
					output, convergenceDelta, 
					maxIterations, runClustering, 
					clusterClassificationThreshold, runSequential);

			Reader.Option filePath= Reader.file(new Path("output/clusters-3-final/part-r-00000"));
			SequenceFile.Reader reader = new SequenceFile.Reader(conf,filePath);

			IntWritable key = new IntWritable();
			ClusterWritable value = new ClusterWritable();
			
			while (reader.next(key, value)) 
				System.out.println("3value : "+value.getValue()+ " key :"
						+ key.toString());
			

			System.out.println("---------");

			filePath= Reader.file(new Path("output/clusters-2/part-r-00000"));
			reader = new SequenceFile.Reader(conf,filePath);

			while (reader.next(key, value)) 
				System.out.println("2value : "+value.getValue()+ " key :"
						+ key.toString());
			System.out.println("---------");
			
			filePath= Reader.file(new Path("output/clusters-1/part-r-00000"));
			reader = new SequenceFile.Reader(conf,filePath);

			while (reader.next(key, value)) 
				System.out.println("1value : "+value.getValue()+ " key :"
						+ key.toString());
			
			System.out.println("---------");
						
			filePath= Reader.file(new Path("output/clusters-0/part-00000"));
			reader = new SequenceFile.Reader(conf,filePath);

			while (reader.next(key, value)) 
				System.out.println("0value : "+value.getValue()+ " key :"
						+ key.toString());
			
			System.out.println("---------");
			
			filePath= Reader.file(new Path("output/clusteredPoints/part-m-00000"));
			reader = new SequenceFile.Reader(conf,filePath);
			
			WeightedPropertyVectorWritable v = new  WeightedPropertyVectorWritable();
			while (reader.next(key, v)) 
				System.out.println("points value : "+v.toString()+ " key :"
						+ key.toString());
			
			System.out.println("---------");
			
			
			reader.close();

			
		} catch (IOException e) {
			LOGGER.error("Exception occurred while creating Filesystem "
					+ e.getMessage());
			System.out.println("Excpetion");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		KmeansApp app = new KmeansApp();
		app.createCluster();
	}
}
