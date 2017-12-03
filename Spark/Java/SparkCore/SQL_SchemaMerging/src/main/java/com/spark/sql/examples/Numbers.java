package com.spark.sql.examples;

import java.io.Serializable;
import java.util.List;

public class Numbers implements Serializable{
	private Integer num;

	public Numbers(Integer num) {
		super();
		this.num = num;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}
	
}
