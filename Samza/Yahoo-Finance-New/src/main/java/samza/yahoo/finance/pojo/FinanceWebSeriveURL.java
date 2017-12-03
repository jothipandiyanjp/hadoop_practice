package samza.yahoo.finance.pojo;

public class FinanceWebSeriveURL {

	private String classname;
	private String fields;
	private String change;
	private String chg_percent;
	private String day_high;
	private String day_low;
	private String issuer_name;
	private String name;
	private String price;
	private String symbol;
	private String ts;
	private String type;
	private String utctime;
	private String volume;
	private String year_high;
	private String year_low;

	public FinanceWebSeriveURL() {
	}

	public FinanceWebSeriveURL(String classname, String fields, String change,
			String chg_percent, String day_high, String day_low,
			String issuer_name, String name, String price, String symbol,
			String ts, String type, String utctime, String volume,
			String year_high, String year_low) {
		super();
		this.classname = classname;
		this.fields = fields;
		this.change = change;
		this.chg_percent = chg_percent;
		this.day_high = day_high;
		this.day_low = day_low;
		this.issuer_name = issuer_name;
		this.name = name;
		this.price = price;
		this.symbol = symbol;
		this.ts = ts;
		this.type = type;
		this.utctime = utctime;
		this.volume = volume;
		this.year_high = year_high;
		this.year_low = year_low;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getChange() {
		return change;
	}

	public void setChange(String change) {
		this.change = change;
	}

	public String getChg_percent() {
		return chg_percent;
	}

	public void setChg_percent(String chg_percent) {
		this.chg_percent = chg_percent;
	}

	public String getDay_high() {
		return day_high;
	}

	public void setDay_high(String day_high) {
		this.day_high = day_high;
	}

	public String getDay_low() {
		return day_low;
	}

	public void setDay_low(String day_low) {
		this.day_low = day_low;
	}

	public String getIssuer_name() {
		return issuer_name;
	}

	public void setIssuer_name(String issuer_name) {
		this.issuer_name = issuer_name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUtctime() {
		return utctime;
	}

	public void setUtctime(String utctime) {
		this.utctime = utctime;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getYear_high() {
		return year_high;
	}

	public void setYear_high(String year_high) {
		this.year_high = year_high;
	}

	public String getYear_low() {
		return year_low;
	}

	public void setYear_low(String year_low) {
		this.year_low = year_low;
	}

	@Override
	public String toString() {
		return "FinanceWebSeriveURL [classname=" + classname + ", fields="
				+ fields + ", change=" + change + ", chg_percent="
				+ chg_percent + ", day_high=" + day_high + ", day_low="
				+ day_low + ", issuer_name=" + issuer_name + ", name=" + name
				+ ", price=" + price + ", symbol=" + symbol + ", ts=" + ts
				+ ", type=" + type + ", utctime=" + utctime + ", volume="
				+ volume + ", year_high=" + year_high + ", year_low="
				+ year_low + "]";
	}

}
