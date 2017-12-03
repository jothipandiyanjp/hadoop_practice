package com.bizruntime.kafka;

import java.io.IOException;

import java.io.InputStream;
import java.util.Properties;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.ZkSerializer;

public class ZooKeeperClient {


	
	public void createTopic(String topicName)  {
		int sessionTimeoutMs = 10000;
		int connectionTimeoutMs = 10000;

		ZkClient zkClient = new ZkClient("localhost:2181", sessionTimeoutMs,
				connectionTimeoutMs, new ZkSerializer() {
					public byte[] serialize(Object data) {
						return ZKStringSerializer.serialize(data);
					}

					public Object deserialize(byte[] bytes) {
						return ZKStringSerializer.deserialize(bytes);
					}
				});

		int numOfPartitions = 4;
		int replicationFactor = 1;
		Properties topicConfig = new Properties();
		InputStream in = ZooKeeperClient.class.getClassLoader()
				.getResourceAsStream("topicConfiguration.properties");
		try {
			topicConfig.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}

		AdminUtils.createTopic(zkClient, topicName, numOfPartitions, replicationFactor, topicConfig);

	}
}
