package com.amazonaws.samples;

import java.io.*;
import java.net.*;
import java.util.zip.InflaterInputStream;
import java.util.zip.DeflaterOutputStream;

import org.json.*;

/*import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.GetObjectRequest;
*/

public class Game {
	float buyersFee = 50;
	
	protected static boolean buyStocks(String asxCode, int number){
		int index;
		float price, totalPrice;
		for (index = 0; index < AsxGame.stockArray.size(); index++){							
			if (asxCode.equals(AsxGame.stockArray.get(index).code)){
				price = AsxGame.stockArray.get(index).askPrice;
				totalPrice = price * number;
				if (totalPrice <= AsxGame.activePlayer.balance){
					if (AsxGame.activePlayer.addShares(asxCode, number)){
						AsxGame.activePlayer.removeBalance(totalPrice + calcBrokersFeePurch(totalPrice));
						AsxGame.activePlayer.calcValue();
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
				int currentStocks = AsxGame.activePlayer.getShareCount(asxCode);
				if (number > currentStocks){
					number = currentStocks;
				}
				totalPrice = price * number;
				if (AsxGame.activePlayer.removeShares(asxCode, number)){
					AsxGame.activePlayer.addBalance(totalPrice - calcBrokersFeeSale(totalPrice));
					AsxGame.activePlayer.calcValue();
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
	
	protected static boolean login(String uEmail, String password){
		Socket connection = null;
		boolean successState = false;
		long startTime = System.currentTimeMillis();
		long currentTime, elapsedTime;
		try {
			connection = new Socket(AsxGame.connectionName, AsxGame.portNumber);
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
			connection = new Socket(AsxGame.connectionName, AsxGame.portNumber);
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
							successState = true;
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
	
	protected static boolean saveActivePlayer(){
		Socket connection = null;
		boolean successState = false;
		long startTime = System.currentTimeMillis();
		long currentTime, elapsedTime;
		try {
			connection = new Socket(AsxGame.connectionName, AsxGame.portNumber);
			System.out.println("Local Address: " + connection.getLocalAddress());
			System.out.println("Local Port: " + connection.getLocalPort());
			
			//Input Streams
			BufferedReader connectionRead = null;
			String response = null;
			
    		//Output Streams
			DeflaterOutputStream deflStream = null;
			
			try {
				String emailHash = Integer.toString(AsxGame.activePlayer.email.hashCode());
				
				//register call
				deflStream = new DeflaterOutputStream(connection.getOutputStream(), true);
				System.out.println("Attempt save...");
				String playerSaveString = AsxGame.activePlayer.generateSaveString();
				String saveString = "save\n" + emailHash + "\n" + playerSaveString;
				System.out.println("Using: " + saveString);
				byte[] saveBytes = saveString.getBytes("UTF-8");
				deflStream.write(saveBytes);
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
							System.out.println("Save Successful!");
							successState = true;
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
		float balance = Float.parseFloat(json.getString("Balance"));
		String shareString = json.getString("Shares");
		String score = json.getString("Score");
		String rights = json.getString("Rights");

		AsxGame.activePlayer = new Player(name,surname,email,balance,shareString,score,rights);
		AsxGame.activePlayerLoaded = true;
		
		return true;
	}
	
	protected static boolean getValueLeaderboard(){
		AsxGame.leaderboard.clear();
		Socket connection = null;
		boolean successState = false;
		long startTime = System.currentTimeMillis();
		long currentTime, elapsedTime;
		try {
			connection = new Socket(AsxGame.connectionName, AsxGame.portNumber);
			System.out.println("Local Address: " + connection.getLocalAddress());
			System.out.println("Local Port: " + connection.getLocalPort());
			
			//Input Streams
			BufferedReader connectionRead = null;
			String response = null;
			
    		//Output Streams
			DeflaterOutputStream deflStream = null;
			
			try {				
				//leaderboard call
				deflStream = new DeflaterOutputStream(connection.getOutputStream(), true);
				String leaderString = "leaders\n" + "0" + "\n" + "10"; //'0' is top position returned, '10' is number of places returned
				byte[] leaderBytes = leaderString.getBytes("UTF-8");
				deflStream.write(leaderBytes);
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
							System.out.println("Leaderboard download successful!");
							System.out.println(response);	
							try {
								String[] leadersJSON = response.split(";");
								for (int i = 0; i < leadersJSON.length; i++){
									JSONObject json = new JSONObject(leadersJSON[i]);
									String name = json.getString("Name");
									String sName = json.getString("Surname");
									String score = json.getString("Score");
									String foo = name + " " + sName + ":\t" + score;
									AsxGame.leaderboard.add(foo);
								}
								successState = true;
								break;
							} catch (JSONException e){
								e.printStackTrace();
								successState = false;
								break;
							}
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
	
	private static float calcBrokersFeePurch(float transactionAmount){ 	//$50 + %1
		float output = 50;
		float fee = (float) (transactionAmount * 0.01);
		output += fee;
		return output;
	}
	
	private static float calcBrokersFeeSale(float transactionAmount){ 	//$50 + %1
		float output = 50;
		float fee = (float) (transactionAmount * 0.0025);
		output += fee;
		return output;
	}
}

