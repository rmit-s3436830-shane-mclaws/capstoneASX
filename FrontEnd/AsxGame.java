package com.amazonaws.samples;
import java.util.*;
import java.io.*;

public class AsxGame {

	//Make these not plain text somehow, even just remove them from the version that gets uploaded to github
	public static String accessKey = "Removed for upload to github";
	public static String secretAccessKey = "removed for upload to github";
	public static String connectionName = "ec2-13-54-16-160.ap-southeast-2.compute.amazonaws.com";
	public static int portNumber = 28543;
	
	
	final static String[] stockList = AsxPull.getStockList();
	public static ArrayList<Stock> stockArray = new ArrayList<Stock>();
	public static Player activePlayer;
	public static boolean activePlayerLoaded = false;
			
	public static void main(String[] args){
		System.out.println(
				"Welcome to Programming Project 1 - Stock Market game \"Trading Wheels\"\n\n"
				
				+ "A Project by:\n"
				+ "		Shane McLaws (s3436830)\n"
				+ "		Callum Pullyblank (s3378543)\n"
				+ "		Zac Williams (s3431670)\n"
				+ "		Sonia Varghese (s3484881)\n\n"
				+ "Press Enter to continue..."
		);
		new Thread(new AsxPullThread()).start();
		try{
			System.in.read();
		} catch(IOException e) {
			e.printStackTrace();
		}
		while (true){
			if (activePlayerLoaded == false){
				Menus.menuLogin();
			} else {
				Menus.mainMenu();
			}
		}
	}
}
