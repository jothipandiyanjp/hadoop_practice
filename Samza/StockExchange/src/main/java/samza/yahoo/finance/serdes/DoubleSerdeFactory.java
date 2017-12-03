package samza.yahoo.finance.serdes;

import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

public class DoubleSerdeFactory implements SerdeFactory<Double> {
	@Override
	public Serde<Double> getSerde(String name, Config config) {
		return new DoubleSerde();
	}
}
