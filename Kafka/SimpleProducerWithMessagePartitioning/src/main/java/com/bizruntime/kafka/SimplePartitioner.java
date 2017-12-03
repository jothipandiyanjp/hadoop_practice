package com.bizruntime.kafka;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

public class SimplePartitioner implements Partitioner {
    public SimplePartitioner (VerifiableProperties props) {
    	 
    }
	public int partition(Object key, int numPartitions) {
	
		int partition = 0;
		int iKey = Integer.parseInt((String)key);
		if (iKey > 0) {
		partition = iKey % numPartitions;
		}
			return partition;
	}



}
