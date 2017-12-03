package schema.add.fields;

import java.nio.charset.Charset;

import schema.add.sample.SeatReserved;
import akka.serialization.SerializerWithStringManifest;

public class AddedFieldsSerializer extends SerializerWithStringManifest {

	private final Charset utf8 = Charset.forName("UTF-8");

	@Override
	public int identifier() {
		return 67868;
	}

	@Override
	public String manifest(Object o) {
		return o.getClass().getName();
	}

	private final String seatReserevedManifest = SeatReserved.class.getName();

	@Override
	public Object fromBinary(byte[] bytes, String manifest) {
		if (seatReserevedManifest.equals(manifest)) {
			try {
				String seatReserved = new String(bytes,utf8);
				return seatReserved(seatReserved.split("[|]"));	
				
			} catch (Exception e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		} else {
			throw new IllegalArgumentException("Unable to handle manifest: "
					+ manifest);
		}
	}


	@Override
	public byte[] toBinary(Object o) {

		if (o instanceof SeatReserved) {
			SeatReserved s = (SeatReserved) o;
			return (s.getLetter() + "|" + s.getRow()+"|"+SeatType.toCode(s.getSeatType())).getBytes(utf8);
		} else {
			throw new IllegalArgumentException(
					"Unable to serialize to bytes, class was: "
							+ o.getClass().getName());
		}
	}

	private SeatReserved seatReserved(String[] split) throws Exception {
		
		if (split.length == 2){
			return new SeatReserved(split[0], Integer.parseInt(split[1]),SeatType.fromCode(""));
	}
		else if(split.length == 3){
			return new SeatReserved(split[0], Integer.parseInt(split[1]),SeatType.fromCode(split[2]));
		}			
		else{
			throw new Exception();
		}
	}
	

}
