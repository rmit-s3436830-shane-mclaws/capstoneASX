package com.amazonaws.samples;

import java.io.*;
import java.net.*;
import java.util.zip.InflaterInputStream;
import java.util.zip.DeflaterOutputStream;

import org.json.*;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.GetObjectRequest;


public class Game {
	
	protected static boolean buyStocks(String asxCode, int number){
		int index;
		float price, totalPrice;
													System.out.println(asxCode.length());
													System.out.println(asxCode + " " + number);
		for (index = 0; index < AsxGame.stockArray.size(); index++){
													System.out.println("For loop entered");
													System.out.println(AsxGame.stockArray.get(index).code);
													System.out.println(AsxGame.stockArray.get(index).code.length());								
			if (asxCode.equals(AsxGame.stockArray.get(index).code)){
													System.out.println("asxCode = AsxGame.stockArray.get(index).code");
				price = AsxGame.stockArray.get(index).askPrice;
				totalPrice = price * number;
													System.out.println(totalPrice);
				if (totalPrice <= AsxGame.activePlayer.balance){
													System.out.println("totalPrice < balance");
					if (AsxGame.activePlayer.addShares(asxCode, number)){
						AsxGame.activePlayer.removeBalance(totalPrice);
						return true;
					} else {
						return false;
					}
				} else {
					System.out.println("Error: Insufficient Funds");
					return false;
				}
			}
		}
		return false;		
	}
	
	protected static boolean sellStocks(String asxCode, int number){
		int index;
		float price, totalPrice;
		for (index = 0; index < AsxGame.stockArray.size(); index++){
			if (asxCode.equals(AsxGame.stockArray.get(index).code)){
				price = AsxGame.stockArray.get(index).askPrice;
				totalPrice = price * number;
				if (AsxGame.activePlayer.removeShares(asxCode, number)){
					AsxGame.activePlayer.addBalance(totalPrice);
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
	
	protected static boolean getAsxJson(String asxCode, String fileName){
		//Define variables
		String bucket = "asx-json-host";
		String filePath = asxCode + "/" + fileName + ".json";
		
		//Establish connection to S3
		System.out.println(filePath);
		String accessKey = "Removed for upload to GitHub";
		String secretAccessKey = "Removed for upload to GitHub";
		//Make these not plain text somehow, even just remove them from the version that gets uploaded to github
		AWSCredentials credentials = new BasicAWSCredentials(accessKey,secretAccessKey);
		
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
												.withCredentials(new AWSStaticCredentialsProvider(credentials))
												.withRegion(Regions.AP_SOUTHEAST_2)
												.build();
		//below is Cal's original code, AmazonS3Client is deprecated
		//AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
		
		//Get object into stream
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
			
			JSONObject json = new JSONObject(currentLine);
			String time = json.getString("Time");
			String name = json.getString("Name");
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
			Stock stockToAdd = new Stock(time, name, asxCode, askPrice, bidPrice, openValue, dayHigh, dayLow);
			AsxGame.stockArray.add(stockToAdd);
			System.out.println(stockToAdd.code.length());
		} catch (IOException e){
			e.getStackTrace();
		}
		return true;
	}
	
	protected static boolean login(String uEmail, String password){
		Socket connection = null;
		boolean successState = false;
		long startTime = System.currentTimeMillis();
		long currentTime, elapsedTime;
		try {
			connection = new Socket("ec2-52-62-111-69.ap-southeast-2.compute.amazonaws.com", 28543);
			System.out.println("Local Address: " + connection.getLocalAddress());
			System.out.println("Local Port: " + connection.getLocalPort());
			
			//Input Streams
			BufferedReader connectionRead = null;
			String response = null;
			
    		//Output Streams
			DeflaterOutputStream deflStream = null;
			
			try {
				String emailHash = Integer.toString(uEmail.hashCode());
				String pwHash = Integer.toString(password.hashCode());
				
				//login call
				deflStream = new DeflaterOutputStream(connection.getOutputStream(), true);
				System.out.println("Attempt login...");
				System.out.println("Using ID: " + uEmail);
				System.out.println("Using email hash: " + emailHash);
				System.out.println("Using password hash: " + pwHash);
				String loginString = "login\n" + emailHash + "\n" + pwHash;
				byte[] loginBytes = loginString.getBytes("UTF-8");
				deflStream.write(loginBytes);
				deflStream.finish();
				deflStream.flush();
				
				while(true)
				{
					connectionRead = new BufferedReader(new InputStreamReader(new InflaterInputStream(connection.getInputStream())));
					response = connectionRead.readLine();
					if(response != null)
					{
						if(!response.equals("401"))
						{
							System.out.println("Login successful!");
							System.out.println("Data received: " + response);
							loadPlayer(response);
							successState = true;
							break;
						}
						else
						{
							System.out.println("401: UNAUTHORIZED!");
							successState = false;
							break;
						}
					}
					currentTime = System.currentTimeMillis();
					elapsedTime = currentTime - startTime;
					if (elapsedTime > 10000){
						System.out.println("Timeout Error!");
						successState = false;
						break;
					}
				}
			} catch (IOException e){
				System.out.println("Exception while reading input: " + e);
				successState = false;
			} finally {
				try{
					connectionRead.close();
    			} catch (IOException e) {
    				System.out.println("Exception while closing streams: " +e);
    			}
			}
		} catch (IOException e) {
			System.out.println("Exception while opening connection: " + e);
			successState = false;
		} finally {
			try	{
        		connection.close();
        	} catch (IOException e)	{
        		System.out.println("Exception while closing connection: " + e);
        	}
		}
		return successState;
	} 
	
	protected static boolean registerPlayer(String fName, String sName, String uEmail, String password){
		Socket connection = null;
		boolean successState = false;
		long startTime = System.currentTimeMillis();
		long currentTime, elapsedTime;
		try {
			connection = new Socket("ec2-52-62-111-69.ap-southeast-2.compute.amazonaws.com", 28543);
			System.out.println("Local Address: " + connection.getLocalAddress());
			System.out.println("Local Port: " + connection.getLocalPort());
			
			//Input Streams
			BufferedReader connectionRead = null;
			String response = null;
			
    		//Output Streams
			DeflaterOutputStream deflStream = null;
			
			try {
				String pwHash = Integer.toString(password.hashCode());
				
				//register call
				deflStream = new DeflaterOutputStream(connection.getOutputStream(), true);
				System.out.println("Attempt registration...");
				String registerString = "register\n" + pwHash + "\n" + fName + "\n" + sName + "\n" + uEmail;
				System.out.println("Using: " + registerString);
				byte[] registerBytes = registerString.getBytes("UTF-8");
				deflStream.write(registerBytes);
				deflStream.finish();
				deflStream.flush();
				
				while(true)
				{
					connectionRead = new BufferedReader(new InputStreamReader(new InflaterInputStream(connection.getInputStream())));
					response = connectionRead.readLine();
					if(response != null)
					{
						if(!response.equals("500"))
						{
							System.out.println("Registration successful!");
							login(uEmail, password);
							break;
						}
						else
						{
							System.out.println("500: INTERNAL SERVER ERROR!");
							break;
						}
					}
					currentTime = System.currentTimeMillis();
					elapsedTime = currentTime - startTime;
					if (elapsedTime > 10000){
						System.out.println("Timeout Error!");
						successState = false;
						break;
					}
				}
			} catch (IOException e){
				System.out.println("Exception while reading input: " + e);
				successState = false;
			} finally {
				try{
					connectionRead.close();
    			} catch (IOException e) {
    				System.out.println("Exception while closing streams: " +e);
    			}
			}
		} catch (IOException e) {
			System.out.println("Exception while opening connection: " + e);
			successState = false;
		} finally {
			try	{
        		connection.close();
        	} catch (IOException e)	{
        		System.out.println("Exception while closing connection: " + e);
        	}
		}
		return successState;
	}
	
	protected static boolean loadPlayer(String response){
		//gets response from login function, gets values for each variable
		//and creates active player
		
		JSONObject json = new JSONObject(response);
		String name = json.getString("Name");
		String surname = json.getString("Surname");
		String email = json.getString("Email");
		float balance = (float) Integer.parseInt(json.getString("Balance"));
		//String[] shares = json.get("Shares");
		String rights = json.getString("Rights");

		AsxGame.activePlayer = new Player(name,surname,email,balance,rights);
		AsxGame.activePlayerLoaded = true;
		
		return true;
	}
}
