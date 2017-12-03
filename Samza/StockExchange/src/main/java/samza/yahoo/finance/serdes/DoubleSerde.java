package samza.yahoo.finance.serdes;

import kafka.serializer.Encoder;

import org.apache.commons.lang.SerializationUtils;
import org.apache.samza.serializers.Serde;

public class DoubleSerde implements Serde<Double> ,Encoder<Double>{

	@Override
	public byte[] toBytes(Double d) {
        return SerializationUtils.serialize(d);
	}

	@Override
	public Double fromBytes(byte[] bytes) {
		return (Double) SerializationUtils.deserialize(bytes);
	}

}
