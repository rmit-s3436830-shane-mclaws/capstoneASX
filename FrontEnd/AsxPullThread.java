package com.amazonaws.samples;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

//import java.io.File;

public class AsxPullThread implements Runnable{
	public void run(){
		PrintWriter writer;
		try {
			writer = new PrintWriter("asxErrorLog.log");
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e) {
		}		
		AsxPull.loadStocks();
		System.out.println(AsxGame.stockArray.size());
		
		//Utilities.loadTempStockList();
	}
}
