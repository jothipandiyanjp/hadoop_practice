package schema.add.sample;

import java.io.Serializable;

import schema.add.fields.SeatType;

public class SeatReserved implements Serializable {
	public final String letter;
	public final int row;
	public final SeatType seatType;

	public SeatReserved(String letter, int row,SeatType seatType) {
	    this.letter = letter;
	    this.row = row;
	    this.seatType = seatType;
	  }


	public String getLetter() {
		return letter;
	}
	public int getRow() {
		return row;
	}


	public SeatType getSeatType() {
		return seatType;
	}


	@Override
	public String toString() {
		return "SeatReserved [letter=" + letter + ", row=" + row
				+ ", seatType=" + seatType + "]";
	}


	

	

	
}
