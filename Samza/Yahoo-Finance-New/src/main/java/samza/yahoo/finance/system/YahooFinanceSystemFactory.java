package samza.yahoo.finance.system;

import org.apache.samza.SamzaException;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.SystemAdmin;
import org.apache.samza.system.SystemConsumer;
import org.apache.samza.system.SystemFactory;
import org.apache.samza.system.SystemProducer;
import org.apache.samza.util.SinglePartitionWithoutOffsetsSystemAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class YahooFinanceSystemFactory implements SystemFactory {
	
	public SystemAdmin getAdmin(String systemName, Config config) {
		return new SinglePartitionWithoutOffsetsSystemAdmin();
	}
	
	public SystemConsumer getConsumer(String systemName, Config config,
			MetricsRegistry registry) {
			
			return new YahooFinanceConsumer(systemName,config,registry);
		
		}
	
	public SystemProducer getProducer(String systemName, Config config,
			MetricsRegistry registry) {
		throw  new SamzaException("not supposed to write to files");
	}
	
}
