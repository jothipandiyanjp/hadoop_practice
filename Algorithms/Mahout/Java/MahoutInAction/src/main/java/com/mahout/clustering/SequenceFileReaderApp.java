package com.mahout.clustering;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.clustering.classify.WeightedVectorWritable;
import org.apache.mahout.clustering.iterator.ClusterWritable;
import org.apache.mahout.math.VectorWritable;

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
					"testData/points/file1"), conf);
			
			LongWritable key = new LongWritable();
			VectorWritable value = new VectorWritable();
			while (reader.next(key, value)) {
				System.out.println("value : "+value.get()+ " key :"
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
