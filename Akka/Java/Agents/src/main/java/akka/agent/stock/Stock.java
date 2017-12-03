package akka.agent.stock;

import akka.agent.Agent;

public class Stock {
	private String symbol;
	private Agent<Float> price;
	public Stock(String symbol, Agent<Float> price) {
		super();
		this.symbol = symbol;
		this.price = price;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Agent<Float> getPrice() {
		return price;
	}
	public void setPrice(Agent<Float> price) {
		this.price = price;
	}
	
	
	
}
