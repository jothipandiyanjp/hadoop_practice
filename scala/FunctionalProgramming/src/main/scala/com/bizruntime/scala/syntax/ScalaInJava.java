package com.bizruntime.scala.syntax;

public class ScalaInJava {
	public static void main(String[] args) {
		ScalaInJava example=new ScalaInJava();
		example.test();
	}
	void test(){
		ScalaExample.v();
		ScalaExample$.MODULE$.v();
		
	}
}
