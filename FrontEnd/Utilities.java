package com.amazonaws.samples;

import java.io.*;
public class Utilities {
	
	public static void loadTempStockList(){
		AsxPull.getAsxJson("ABC", "20170329");
		AsxPull.getAsxJson("AWC", "20170329");
		AsxPull.getAsxJson("ANN", "20170329");
		AsxPull.getAsxJson("ALU", "20170329");
		AsxPull.getAsxJson("AMP", "20170329");
		return;
	}	
	
	public static void createTempOfflinePlayer(){
		AsxGame.activePlayer = new Player("Offline", "Player", "OP@email.com", 1000000,
									"", "5", "trader");
		AsxGame.activePlayerLoaded = true;
		return;
	}
	
	public static void errorToLogFile(String error){
		try{
			Writer writer;
			writer = new BufferedWriter(new FileWriter("errorLog.log", true));
			writer.append(error + "\n");
			writer.close();
			return;
		} catch (IOException e){
			e.printStackTrace();			
			return;
		}
	}
	public static void asxErrorToLogFile(String file, String error){
		try{
			Writer writer;
			writer = new BufferedWriter(new FileWriter("asxErrorLog.log", true));
			writer.append(file + ": " + error + "\n");
			writer.close();
			return;
		} catch (IOException e){
			e.printStackTrace();			
			return;
		}
	}
}
