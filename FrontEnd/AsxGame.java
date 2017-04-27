package com.amazonaws.samples;


import java.awt.EventQueue;
import java.io.IOException;

import java.util.ArrayList;

import org.json.JSONObject;

public class AsxGame {

	//Make these not plain text somehow, even just remove them from the version that gets uploaded to github
	public static String accessKey = creds.accessKey;
	public static String secretAccessKey = creds.secretAccessKey;

	public static String connectionName = "ec2-13-54-16-160.ap-southeast-2.compute.amazonaws.com"; //original code
	public static int portNumber = 28543;
	
	public static boolean asxLoadComplete = false;
	final static String[] stockList = AsxPull.getStockList();
	public static ArrayList<Stock> stockArray = new ArrayList<Stock>();
	
	public static Player activePlayer;
	public static Player activeAdmin;
	public static boolean activePlayerLoaded = false;
	public static boolean activeAdminLoaded = false;
	public static int loadCompletePercent = 0;
	public static ArrayList<JSONObject> leaderboard = new ArrayList<JSONObject>();
	
	//windows defined here
	public static UI_LogIn loginWindow = new UI_LogIn();
	public static UI_SignUp signUpWindow = new UI_SignUp();
	public static UI_MainView mainWindow;
	public static UI_SellWindow sellWindow;
	public static UI_BuyWindow buyWindow;
	public static UI_ViewStocks stockWindow;
	public static UI_Leaderboard leadersWindow;
	public static UI_ViewTransHist transHistWindow;
			
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
		
		System.out.println("Stocklist Length: " + stockList.length);
		Thread loadASXdata = new Thread(new LoadASXData());
		loadASXdata.start();
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					loginWindow.frmLogin.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		
		try{
			System.in.read();
		} catch(IOException e) {
			e.printStackTrace();
		}
		while (true){
			if (activePlayerLoaded == false){
				Menus.menuLogin();
			} else if (activeAdminLoaded == true){
				System.out.println("Admin loaded");
				Menus.adminMenu();
			} else {
				System.out.println("Player loaded");
				Menus.mainMenu();
			}
		}
	}
}
