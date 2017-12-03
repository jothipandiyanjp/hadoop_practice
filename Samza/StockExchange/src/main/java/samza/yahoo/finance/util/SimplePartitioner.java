package samza.yahoo.finance.util;


import kafka.utils.VerifiableProperties;

public class SimplePartitioner  {
    public SimplePartitioner (VerifiableProperties props) {
    	
    }
	 static int partition(Object key, int numPartitions) {
		int partition = 0;
		int iKey = key.hashCode();
		if (iKey > 0) {
		partition = iKey % 4;
		}
			return partition;
	}
public static void main(String[] args) {
	System.out.println(partition("LNKD",4));
}
}
