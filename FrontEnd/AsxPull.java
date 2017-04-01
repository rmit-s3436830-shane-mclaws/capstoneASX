package com.amazonaws.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

public class AsxPull {
	final static String bucket = "asx-json-host";
	final static AWSCredentials credentials = new BasicAWSCredentials(AsxGame.accessKey,AsxGame.secretAccessKey);
	final static AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
														.withCredentials(new AWSStaticCredentialsProvider(credentials))
														.withRegion(Regions.AP_SOUTHEAST_2)
														.build();
	static int deadCompanies = 0;
//below is Cal's original code, AmazonS3Client is deprecated
//AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
	
	protected static boolean getAsxJson(String asxCode, String fileName){
		//Define variables
		
		String filePath = asxCode + "/" + fileName + ".json";
		
		//Establish connection to S3
		System.out.println(filePath);	
		
		//Get object into stream
		try {
			S3Object object = s3Client.getObject(new GetObjectRequest(bucket, filePath));
			InputStream objectData = object.getObjectContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
			
			String line = null;
			String currentLine = null;
			try{
				while((line = reader.readLine()) != null){
					//System.out.println(line);
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
					try{
						Stock stockToAdd = new Stock(time, name, asxCode, askPrice, bidPrice, openValue, dayHigh, dayLow);
						AsxGame.stockArray.add(stockToAdd);
					} catch (NumberFormatException e){
						Utilities.asxErrorToLogFile(filePath, e.toString());
						return false;
					}				
				} catch (JSONException e){
					String error = e.toString();
					Utilities.asxErrorToLogFile(filePath,error);
					return false;
				}
			} catch (IOException e){
				e.getStackTrace();
			}
		} catch (AmazonS3Exception e){
			Utilities.asxErrorToLogFile(filePath, e.toString());
			return false;
		}
		return true;
	}
	
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
	
	public static boolean loadStocks(){					//currently only working for single day, set in this method
		deadCompanies = 0;
		if (AsxGame.stockList != null){
			for (int i = 0; i < AsxGame.stockList.length; i++){
				AsxPull.getAsxJson(AsxGame.stockList[i], "20170401");
				if (AsxGame.stockArray.size() + deadCompanies == 150){				//stop after pulling 150 stock (the only stocks
					break;											//currently available on S3)
				}
			}
		}
		
		return false;
	}
}
