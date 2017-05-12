//Main class
/*
 global variables are held here, including:
  - Player
  - Stock List
  - Connection constants
 */

package com.amazonaws.samples;

import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.stage.Stage;

public class AsxGame extends Application{

	//network connectivity constants
	//Make these not plain text somehow, even just remove them from the version that gets uploaded to github
	final public static String accessKey = creds.accessKey;
	final public static String secretAccessKey = creds.secretAccessKey;
	final public static String connectionName = "ec2-13-54-16-160.ap-southeast-2.compute.amazonaws.com"; //original code
	final public static int portNumber = 28543;
	
	//asxStock data global variables
	public static boolean asxLoadComplete = false;						//boolean status of asx data download
	public static int loadCompletePercent = 0;							//number status of asx data download
	final static String[] stockList = AsxPull.getStockList();			//array of all stock codes, loaded from CSV in S3 bucket
	public static ArrayList<Stock> stockArray = new ArrayList<Stock>();
	public static String requestedStockCode;
	public static ArrayList<JSONObject> requestedStockHistory = new ArrayList<JSONObject>();
	
	//	player/admin global variables
	public static Player activePlayer;						//holds the Player object for currently loaded player
	public static Player activeAdmin;						//holds the Player object for currently loaded admin
	public static boolean activePlayerLoaded = false;		//state of whether or not a player is loaded
	public static boolean activeAdminLoaded = false;		//state of whether or not an admin is loaded
	
	//leaderboard array (stored as JSON Objects)
	//Keys: "Name" (as String), "Surname" (as String), "Score" (as String)
	public static ArrayList<JSONObject> leaderboard = new ArrayList<JSONObject>();
	
	//UI Stuff defined here
	public static Stage mainStage;
	public static UI_Login UI_loginScene = new UI_Login();
	public static UI_Register UI_RegisterScene = new UI_Register();
	public static UI_MainScene UI_MainScene;
	
	//Threads defined here
	public static Thread loadASXdata = new Thread(new LoadASXData());
	
	@Override
	public void stop() {
		System.exit(0);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		mainStage = primaryStage;
		mainStage.setTitle("ASX Trading Wheels");
		mainStage.setResizable(false);
		mainStage.setScene(UI_loginScene.scene);
		
		// add css to scenes
		UI_loginScene.scene.getStylesheets().add(
				getClass().getResource("UI_LoginStyle.css").toExternalForm());
		UI_RegisterScene.scene.getStylesheets().add(
				getClass().getResource("UI_RegisterStyle.css").toExternalForm());
		
		//initialise Other Bits
		UI_Portfolio.initPortfolioTable();
		UI_HistoryWindow.initHistoryTable();
		
		mainStage.show();
		
	}
				
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
		
		//starts the loading of ASX Data from S3 bucket
		System.out.println("Stocklist Length: " + stockList.length);
		
		loadASXdata.start();
		
		//starts User Interface
		launch(args);
		
		//Command Line stuff
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
