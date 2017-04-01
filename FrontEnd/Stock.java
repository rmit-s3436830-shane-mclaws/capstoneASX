package com.amazonaws.samples;

public class Stock {
	int time;
	
	String name;
	String code;
	
	String pastYearRange;
	String pastYearHigh;
	String pastYearLow;
	String change;
	String changePercent;
	
	float askPrice;
	float bidPrice;
	float openingValue;
	float dayHigh;
	float dayLow;
	float prevClose;
	
	public Stock (String timeIn, String nameIn, String codeIn, String askPriceIn, String bidPriceIn,
					String openingValueIn, String dayHighIn, String dayLowIn){
		String[] timeParts = timeIn.split(":");
		this.time = Integer.parseInt(timeParts[0] + timeParts[1]);
		this.name = nameIn;
		this.code = codeIn;
		if (!askPriceIn.equals("N/A"))
			this.askPrice = Float.parseFloat(askPriceIn);
		if (!bidPriceIn.equals("N/A"))
			this.bidPrice = Float.parseFloat(bidPriceIn);
		if (!openingValueIn.equals("N/A"))
			this.openingValue = Float.parseFloat(openingValueIn);
		if (!dayHighIn.equals("N/A"))
			this.dayHigh = Float.parseFloat(dayHighIn);
		if (!dayLowIn.equals("N/A"))
			this.dayLow = Float.parseFloat(dayLowIn);
	}
	
	public void printStock(){
		System.out.println(
				"Time: " + time + ", Name: " + name + ", Code: " + code + ", askPrice: " + askPrice
		);
	}
}
