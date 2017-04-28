/*
	this is the class for the threads that prepare the asxErrorLog.log file
	and calls the load stock functions
	Prints the total number of stocks downloaded when the thread finishes
 */

package com.amazonaws.samples;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

//import java.io.File;

public class AsxPullThread implements Runnable{
	
	int startPoint, endPoint;
	
	AsxPullThread(int startPoint, int endPoint){
		this.startPoint = startPoint;
		this.endPoint = endPoint;			//set to -1 for end of list
	}
	
	public void run(){
		System.out.println("thread running");
		PrintWriter writer;
		try {
			writer = new PrintWriter("asxErrorLog.log");
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e) {
		}		
		AsxPull.loadStocks(startPoint, endPoint);
		System.out.println(AsxGame.stockArray.size());
		
		//Utilities.loadTempStockList();
	}
}
