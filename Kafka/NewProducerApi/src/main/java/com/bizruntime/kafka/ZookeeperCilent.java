package com.bizruntime.kafka;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer;
import kafka.utils.ZkUtils;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.serialize.ZkSerializer;

public class ZookeeperCilent {

	public void createTopic(String topicName)  {
		int sessionTimeoutMs = 10000;
		int connectionTimeoutMs = 10000;

		ZkClient zkClient=new ZkClient("localhost:2181", sessionTimeoutMs,
				connectionTimeoutMs, new ZkSerializer() {
					public byte[] serialize(Object data) {
						return ZKStringSerializer.serialize(data);
					}

					public Object deserialize(byte[] bytes) {
						return ZKStringSerializer.deserialize(bytes);
					}
				});		
		ZkConnection  zkConnection=new ZkConnection("localhost:2181", sessionTimeoutMs);
		ZkUtils zkutil = new ZkUtils(zkClient, zkConnection, true);
				


		int numOfPartitions = 4;
		int replicationFactor = 2;
		Properties topicConfig = new Properties();
		InputStream in = ZookeeperCilent.class.getClassLoader()
				.getResourceAsStream("topicConfiguration.properties");
		try {
			topicConfig.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}

		AdminUtils.createTopic(zkutil, topicName, numOfPartitions, replicationFactor, topicConfig);

	}
}
