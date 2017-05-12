/*
	AsxPull class
	
	This is where all functions used by each Thread to download ASX Data a kept
	This may need changing ONLY IF you wish to reuse some of it to grab data for multiple days of a stock
	At the moment, it only grabs the most recent stock data
 */

package com.amazonaws.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.*;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import javafx.application.Platform;

public class AsxPull {
	final static String bucket = "asx-json-host";
	final static AWSCredentials credentials = new BasicAWSCredentials(AsxGame.accessKey,AsxGame.secretAccessKey);
	final static AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
														.withCredentials(new AWSStaticCredentialsProvider(credentials))
														.withRegion(Regions.AP_SOUTHEAST_2)
														.build();
	static int deadCompanies = 0;
	static String dateString;
	//below is Cal's original code, AmazonS3Client is deprecated
	//AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
	
	// This is the code for downloading a single stocks data
	// inputs are the asxCode and filename (date) you need to grab
	// Only called from loadStocks()
	protected static boolean getAsxJson(String asxCode, String fileName){
		String filePath =  asxCode + "/" + fileName + ".json";
		
		//Get object into stream
		try {
			S3Object object = s3Client.getObject(new GetObjectRequest(bucket, filePath));
			InputStream objectData = object.getObjectContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
			
			String line = null;
			String currentLine = null;
			try{
				while((line = reader.readLine()) != null){
					if (line != null){
						currentLine = line;
					}				
				}
				//commented lines are not used in current build
				try{
					JSONObject json = new JSONObject(currentLine);
					String time = json.getString("Time");
					String name = json.getString("Name");
					if (name.equals("N/A")){
						deadCompanies++;
						System.out.println(filePath);
						return false;
					}
					//String asxCode = json.getString("ASX Code");
					String askPrice = json.getString("Ask Price");
					String bidPrice = json.getString("Bid Price");
					//String lastTradePrice = json.getString("Last Trade Price");
					//String lastTradeTime = json.getString("Last Trade Time");
					//String change = json.getString("Change");
					//String changePercent = json.getString("Change(%");
					String openValue = json.getString("Opening Value");
					String dayHigh = json.getString("Day High");
					String dayLow = json.getString("Day Low");
				//	String prevClose = json.getString("Previous Close");
				//	String lastYearRange = json.getString("52 Week Range");
				//	String lastYearHigh = json.getString("52 Week High");
				//	String lastYearLow = json.getString("52 Week Low");
				//	String dividendShane = json.getString("Dividend/Share");
				//	String exDividendDate = json.getString("Ex-Dividend Date");
				//	String dividendPayDate = json.getString("Dividende Pay Date");
				//	String dividendYield = json.getString("Dividende Pay Date");
					try
					{
						Stock stockToAdd = new Stock(time, name, asxCode, askPrice, bidPrice, openValue, dayHigh, dayLow);
						AsxGame.stockArray.add(stockToAdd);
					} 
					catch (NumberFormatException e)
					{
						Utilities.asxErrorToLogFile(filePath, e.toString());
						return false;
					}
					catch (ArrayIndexOutOfBoundsException aiobe)
					{
						Utilities.asxErrorToLogFile(filePath, aiobe.toString());
					}
				} catch (JSONException e){
					Utilities.asxErrorToLogFile(filePath, e.toString());
					return false;
				}
			} catch (IOException e){
				Utilities.asxErrorToLogFile(filePath, e.toString());
			}
		} catch (AmazonS3Exception e){
			Utilities.asxErrorToLogFile(filePath, e.toString());
			return false;
		}
		return true;
	}
	
	// downloads the list of stock codes kept on the server
	// this is called to populate AsxGame.stockList
	public static String[] getStockList(){
		String filePath = "companies.csv";
		String line = null;
		String[] output = null;
		S3Object object = s3Client.getObject(new GetObjectRequest(bucket, filePath));
		InputStream objectData = object.getObjectContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
		try {
			line = reader.readLine();
		} catch (IOException e){
			e.getStackTrace();
		}
		if (line != null){
			output = line.split(",");
			return output;
		}
		return output;
	}
	
	// this takes a startPoint and endPoint as integers and loops through
	// those indexes in AsxGame.stockList[] to download those stocks
	// putting a -1 into the endPoint sets the endpoint to be the last stock in the list
	public static boolean loadStocks(int startPoint, int endPoint){					//currently only working for single day, set in this method
		
		getMostRecentDateString();
		if (AsxGame.stockList != null){
			if (endPoint == -1){
				endPoint = AsxGame.stockList.length;
			}
			for (int i = startPoint; i < AsxGame.stockList.length && i < endPoint; i++){
				getAsxJson(AsxGame.stockList[i], dateString);
				
				//this calculates the load percentage and updates the main window title
				Float percentage = 
						((float)AsxGame.stockArray.size()/ (float)AsxGame.stockList.length) * 100;
				AsxGame.loadCompletePercent = Math.round(percentage);
				if (AsxGame.showUI){
					Platform.runLater(new Runnable() {
						@Override
						public void run(){
							AsxGame.mainStage.setTitle(
									"ASX \"Trading Wheels\" - Download in progress: " + AsxGame.loadCompletePercent + "%");
						}
					});
				}
			}
		}
		return false;
	}
	
	// sets variable "dateString" to be the date of the most recent asxData
	// in the format YYYYmmDD
	private static void getMostRecentDateString(){
		LocalDateTime timePoint = LocalDateTime.now();
		LocalDate date = timePoint.toLocalDate();
		
		String[] dateSplit = date.toString().split("-");
		String fileString = dateSplit[0]+dateSplit[1]+dateSplit[2];

		while (!s3Client.doesObjectExist(bucket, AsxGame.stockList[0]+"/"+fileString+".json")){
			date = date.minusDays(1);
			dateSplit = date.toString().split("-");
			fileString = dateSplit[0]+dateSplit[1]+dateSplit[2];
		}		
		System.out.println(fileString);
		dateString = fileString;
		return;
	}
}
