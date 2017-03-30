package com.amazonaws.samples;

public class Utilities {
	
	public static void loadTempStockList(){
		Game.getAsxJson("ABC", "20170329");
		Game.getAsxJson("AWC", "20170329");
		Game.getAsxJson("ANN", "20170329");
		Game.getAsxJson("ALU", "20170329");
		Game.getAsxJson("AMP", "20170329");
		return;
	}	
}
