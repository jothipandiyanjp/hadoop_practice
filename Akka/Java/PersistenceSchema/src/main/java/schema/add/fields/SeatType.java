package schema.add.fields;

public enum SeatType  {

	Window("W"), Aisle("A"), Other("O"), Unknown("");
	private final String code;
	
	private SeatType(String code){
		this.code = code;
	}
	public static SeatType fromCode(String c){
		
		if(Window.code.equals(c))
			return Window;
		else if(Aisle.code.equals(c))
			return Aisle;
		else if(Other.code.equals(c))
			return Other;
		else
			return Unknown;
	}
	public static String toCode(SeatType st){

		if(Window.equals(st))
			return Window.code;
		else if(Aisle.equals(st))
			return Aisle.code;
		else if(Other.equals(st))
			return Other.code;
		else
			return Unknown.code;
	}
}
