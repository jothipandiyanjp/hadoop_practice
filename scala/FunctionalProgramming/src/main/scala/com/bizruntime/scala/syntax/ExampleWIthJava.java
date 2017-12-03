package com.bizruntime.scala.syntax;

import java.util.List;

public class ExampleWIthJava {

}

interface Cat{
	List eat(Birds birds,Rat rat );
	
}
interface Birds{
	String fly(String speed);
}

interface Rat{
	Object run(String speed,int second);
}