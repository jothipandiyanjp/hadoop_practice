package com.mahout.mahout.essentials;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.clustering.classify.WeightedVectorWritable;
import org.apache.mahout.clustering.iterator.ClusterWritable;

public class SequenceFileReaderApp {

	public void readFromSequenceFile() {
		Configuration conf = new Configuration();
		FileSystem fs = null;
		try {
			fs = FileSystem.get(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			SequenceFile.Reader reader = new SequenceFile.Reader(fs, new Path(
					"src/main/resources/essentials/output/clusters-1-final/part-r-00000"), conf);
			IntWritable key = new IntWritable();
			ClusterWritable value = new ClusterWritable();
			while (reader.next(key, value)) {
				System.out.println(value.getValue()+value.toString() + " belongs to cluster "
						+ key.toString());
			}
			reader.close();
		} catch (IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		SequenceFileReaderApp app = new SequenceFileReaderApp();
		app.readFromSequenceFile();
	}
}
