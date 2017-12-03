package spark.core.example.grouping;

import java.io.Serializable;

public class CsvToJava implements Serializable{
	private String name;
	private String location;
	private String category;
	private double ratio;
	public CsvToJava( String name, String location,
			String category, double ratio) {
		super();
		this.name = name;
		this.location = location;
		this.category = category;
		this.ratio = ratio;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public double getRatio() {
		return ratio;
	}
	public void setRatio(double ratio) {
		this.ratio = ratio;
	}
	@Override
	public String toString() {
		return "CsvToJava [ name=" + name
				+ ", location=" + location + ", category=" + category
				+ ", ratio=" + ratio + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CsvToJava)
			return true;
		return false;
	}
	
}
