package samza.yahoo.finance.pojo;

import org.codehaus.jackson.annotate.JsonProperty;

public class YahooFinance {

	private String symbol;

	@JsonProperty(value = "Change")
	private String Change;

	@JsonProperty(value = "DaysLow")
	private String DaysLow;
	@JsonProperty(value = "DaysHigh")
	private String DaysHigh;

	@JsonProperty(value = "Name")
	private String Name;

	@JsonProperty(value = "YearRange")
	private String YearRange;
	@JsonProperty(value = "StockExchange")
	private String StockExchange;

	public YahooFinance() {
	}

	public YahooFinance(String symbol, String change, String daysLow,
			String daysHigh, String name, String yearRange, String stockExchange) {
		super();
		this.symbol = symbol;
		Change = change;
		DaysLow = daysLow;
		DaysHigh = daysHigh;
		Name = name;
		YearRange = yearRange;
		StockExchange = stockExchange;
	}

	public String getDaysLow() {
		return DaysLow;
	}

	public void setDaysLow(String daysLow) {
		DaysLow = daysLow;
	}

	public String getChange() {
		return Change;
	}

	public void setChange(String Change) {
		this.Change = Change;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getDaysHigh() {
		return DaysHigh;
	}

	public void setDaysHigh(String daysHigh) {
		DaysHigh = daysHigh;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getYearRange() {
		return YearRange;
	}

	public void setYearRange(String yearRange) {
		YearRange = yearRange;
	}

	public String getStockExchange() {
		return StockExchange;
	}

	public void setStockExchange(String stockExchange) {
		StockExchange = stockExchange;
	}

	@Override
	public String toString() {
		return "YahooFinance [symbol=" + symbol + ", Change=" + Change
				+ ", DaysLow=" + DaysLow + ", DaysHigh=" + DaysHigh + ", Name="
				+ Name + ", YearRange=" + YearRange + ", StockExchange="
				+ StockExchange + "]";
	}

	
}
