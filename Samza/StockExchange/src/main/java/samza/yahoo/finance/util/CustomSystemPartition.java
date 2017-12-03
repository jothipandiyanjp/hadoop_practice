package samza.yahoo.finance.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.samza.Partition;
import org.apache.samza.SamzaException;
import org.apache.samza.system.SystemAdmin;
import org.apache.samza.system.SystemStreamMetadata;
import org.apache.samza.system.SystemStreamMetadata.OffsetType;
import org.apache.samza.system.SystemStreamMetadata.SystemStreamPartitionMetadata;
import org.apache.samza.system.SystemStreamPartition;

public class CustomSystemPartition implements SystemAdmin{
	
	  private final Map<Partition, SystemStreamPartitionMetadata> systemStreamPartitionMetadata ;
	
	  public CustomSystemPartition(int partitionCount) {
		    this.systemStreamPartitionMetadata = new HashMap<Partition, SystemStreamPartitionMetadata>();

		    for (int i = 0; i < partitionCount; ++i) {
		      systemStreamPartitionMetadata.put(new Partition(i), new SystemStreamPartitionMetadata("oldest","newest","upcoming"));
		    }
		  }
	
	@Override
	  public Map<String, SystemStreamMetadata> getSystemStreamMetadata(Set<String> streamNames) {
		
	    Map<String, SystemStreamMetadata> metadata = new HashMap<String, SystemStreamMetadata>();
	    for (String streamName : streamNames) {
	      metadata.put(streamName, new SystemStreamMetadata(streamName, systemStreamPartitionMetadata));
	    }

	    return metadata;
	  }

	  @Override
	  public void createChangelogStream(String streamName, int numOfPartitions) {
	    throw new SamzaException("Method not implemented");
	  }

	  @Override
	  public void validateChangelogStream(String streamName, int numOfPartitions) {
	    throw new SamzaException("Method not implemented");
	  }

	  @Override
	  public Map<SystemStreamPartition, String> getOffsetsAfter(Map<SystemStreamPartition, String> offsets) {
	    Map<SystemStreamPartition, String> offsetsAfter = new HashMap<SystemStreamPartition, String>();

	    for (SystemStreamPartition systemStreamPartition : offsets.keySet()) {
	      offsetsAfter.put(systemStreamPartition, "oldest");
	    }

	    return offsetsAfter;
	  }

	  @Override
	  public void createCoordinatorStream(String streamName) {
	    throw new UnsupportedOperationException("Single partition admin can't create coordinator streams.");
	  }

	  @Override
	  public Integer offsetComparator(String offset1, String offset2) {
	    return null;
	  }}
