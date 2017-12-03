package com.spark.stream.examples;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;

import scala.Tuple2;
import twitter4j.Status;

public class HashTagJoinSentiments {

	private final Logger LOGGER = Logger.getLogger("HashTagJoinSentiments");

	String consumerKey = "TiVC43WfkWAP5iNZfcmVnWI79";
	String consumerSecret = "XRklR48SEgaDUdztRkjfVcDpF3RLpR6AFM999Rn3SlC5Y93GMR";
	String accessToken = "705749982565892096-RJbUZRXH94jRySUjzVX1TDNzj2N1fY2";
	String accessTokenSecret = "SZpC4PcBOiNnWcJPlietA8uKdUBzBeXTlwJEeLZWozLNr";

	String[] filters = Arrays.copyOfRange(new String[] { consumerKey,
			consumerSecret, accessToken, accessTokenSecret }, 4, 4);

	public void example() {

		System.setProperty("twitter4j.oauth.consumerKey", consumerKey);
		System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret);
		System.setProperty("twitter4j.oauth.accessToken", accessToken);
		System.setProperty("twitter4j.oauth.accessTokenSecret",
				accessTokenSecret);

		SparkConf sparkConf = new SparkConf().setAppName(
				"HashTagJoinSentiments").setMaster("local[2]");

		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf,
				new Duration(2000));

		JavaReceiverInputDStream<Status> stream = TwitterUtils
				.createStream(jssc);

		JavaDStream<String> words = stream.flatMap((Status s) -> Arrays
				.asList(s.getText().split(" ")));

		JavaDStream<String> hashTags = words.filter((String word) -> word
				.startsWith("#"));

		String wordSentimentFilePath = "data/streaming/AFINN-111.txt";

		final JavaPairRDD<String, Double> wordSentiments = jssc
				.sparkContext()
				.textFile(wordSentimentFilePath)
				.mapToPair(
						(String line) -> {
							String[] columns = line.split(" ");
							return new Tuple2<String, Double>(columns[0],
									Double.parseDouble(columns[1]));
						});

		JavaPairDStream<String, Integer> hashTagCount = hashTags.mapToPair((
				String s) -> new Tuple2<String, Integer>(s.substring(1), 1)); // leave
																				// out
																				// the
																				// #
																				// character

		JavaPairDStream<String, Integer> hashTagTotals = hashTagCount
				.reduceByKeyAndWindow((Integer a, Integer b) -> a + b,
						new Duration(10000));

		JavaPairDStream<String, Tuple2<Double, Integer>> joinedTuples = hashTagTotals
				.transformToPair((JavaPairRDD<String, Integer> topicCount) -> wordSentiments
						.join(topicCount));

		// joinedTuples.print();
		// joinedTuples.count().print();

		JavaPairDStream<String, Double> topicHappiness = joinedTuples
				.mapToPair((
						Tuple2<String, Tuple2<Double, Integer>> topicAndTuplePair) -> {

					Tuple2<Double, Integer> happinessAndCount = topicAndTuplePair
							._2();

					System.out.println("->" + topicAndTuplePair._1 + "  "
							+ topicAndTuplePair._2);
					return new Tuple2<String, Double>(topicAndTuplePair._1(),
							happinessAndCount._1() * happinessAndCount._2());
				});

		JavaPairDStream<Double, String> happinessTopicPairs = topicHappiness
				.mapToPair((Tuple2<String, Double> topicHappiness1) -> new Tuple2<Double, String>(
						topicHappiness1._2(), topicHappiness1._1()));

		JavaPairDStream<Double, String> happiest10 = happinessTopicPairs
				.transformToPair((JavaPairRDD<Double, String> happinessAndTopics1) -> happinessAndTopics1
						.sortByKey(false));

		happiest10.foreachRDD((t) -> {
			List<Tuple2<Double, String>> topList = t.take(100);
			System.out.println(String.format(
					"\nHappiest topics in last 10 seconds (%s total):",
					t.count()));

			t.saveAsTextFile("src/main/resources/a.txt");
			for (Tuple2<Double, String> pair : topList) {
				System.out.println(String.format("%s (%s happiness)",
						pair._2(), pair._1()));
			}
		}

		);
		jssc.start();
		jssc.awaitTermination();

	}

	public static void main(String[] args) {
		HashTagJoinSentiments app = new HashTagJoinSentiments();
		app.example();
	}
}
