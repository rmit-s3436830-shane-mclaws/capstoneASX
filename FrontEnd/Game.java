/*
	Main game controls are here
	Including:
		- Buying and Selling functions
		- login,register and save functions
		- leaderBoard download function
		- get current price for specified stock function
	
 */

package com.amazonaws.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.zip.InflaterInputStream;
import java.util.zip.DeflaterOutputStream;

import org.json.*;

public class Game
{
	// function for buying stocks
	// pass an asxCode and number of stocks to buy and it will
	// add those stocks to the player, calculate and subtract money from balance
	// and add the transaction to the players history
	// returns TRUE or FALSE depending on if it could successfully buy the stocks
	protected static boolean buyStocks(String asxCode, int number)
	{
		int index;
		float price, totalPrice, totalPriceWithFee;
		for (index = 0; index < AsxGame.stockArray.size(); index++)
		{							
			if (asxCode.equals(AsxGame.stockArray.get(index).code))
			{	//searches stockArray for stock
				price = AsxGame.stockArray.get(index).askPrice;			//gets stocks price
				totalPrice = price * number;							//calculates the price of that many stocks
				totalPriceWithFee = totalPrice + calcBrokersFeePurch(totalPrice); //calculates the total price including fee
				
				if (totalPriceWithFee <= AsxGame.activePlayer.balance)
				{		//checks that player can afford it
					//add shares and update player
					if (AsxGame.activePlayer.addShares(asxCode, number))
					{	
						AsxGame.activePlayer.removeBalance(totalPrice + calcBrokersFeePurch(totalPrice));
						AsxGame.activePlayer.calcValue();
						AsxGame.activePlayer.updateTransHist("-1", "-1", asxCode, "Buy", number, price);
						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					System.out.println("Error: Insufficient Funds");
					return false;
				}
			}
		}
		return false;		
	}
	
	// function for selling stocks
	// pass an asxCode and number of stocks to sell and it will
	// remove those stocks from the player, calculate and add money to balance
	// and add the transaction to the players history
	protected static boolean sellStocks(String asxCode, int number)
	{
		int index;
		float price, totalPrice;
		if (number <= AsxGame.activePlayer.getShareCount(asxCode))
		{
			for (index = 0; index < AsxGame.stockArray.size(); index++)
			{
				if (asxCode.equals(AsxGame.stockArray.get(index).code))
				{
					price = AsxGame.stockArray.get(index).askPrice;
					int currentStocks = AsxGame.activePlayer.getShareCount(asxCode);
					if (number > currentStocks)
					{
						number = currentStocks;
					}
					totalPrice = price * number;
					if (AsxGame.activePlayer.removeShares(asxCode, number))
					{
						AsxGame.activePlayer.addBalance(totalPrice - calcBrokersFeeSale(totalPrice));
						AsxGame.activePlayer.calcValue();
						AsxGame.activePlayer.updateTransHist("-1", "-1", asxCode, "Sell", number, price);
						return true;
					}
					else
					{
						return false;
					}
				}
			}
		}
		return false;
	}
	
	// login function
	// pass in string and password
	// if successfully logged in, will load player into AsxGame.activePlayer
	// returns TRUE if login is successful, otherwise FALSE
	protected static boolean login(String uEmail, String password)
	{
		Socket connection = null;
		boolean successState = false;
		long startTime = System.currentTimeMillis();
		long currentTime, elapsedTime;
		try 
		{
			connection = new Socket(AsxGame.connectionName, AsxGame.portNumber);
			System.out.println("Local Address: " + connection.getLocalAddress());
			System.out.println("Local Port: " + connection.getLocalPort());
			
			//Input Streams
			BufferedReader connectionRead = null;
			String response = null;
			
    		//Output Streams
			DeflaterOutputStream deflStream = null;
			
			try
			{
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
							connectionRead.readLine();
							while(!((response = connectionRead.readLine()).equals("value")))
                            {
								JSONObject histLine = new JSONObject(response);
								AsxGame.activePlayer.transHistory.add(histLine);
                               // transaction += line + '\n';
                                //Handle individual lines of transaction history here
                            }
							while((response = connectionRead.readLine()) != null)
							{
								JSONObject valLine = new JSONObject(response);
								AsxGame.activePlayer.valueHistory.add(valLine);
							}
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
					if (elapsedTime > 10000)
					{
						System.out.println("Timeout Error!");
						successState = false;
						break;
					}
				}
			}
			catch (IOException e)
			{
				System.out.println("Exception while reading input: " + e);
				successState = false;
			}
			finally
			{
				try
				{
					connectionRead.close();
    			}
				catch (IOException e)
				{
    				System.out.println("Exception while closing streams: " +e);
    			}
			}
		}
		catch (IOException e)
		{
			System.out.println("Exception while opening connection: " + e);
			successState = false;
		}
		finally
		{
			try
			{
        		connection.close();
        	}
			catch (IOException e)
			{
        		System.out.println("Exception while closing connection: " + e);
        	}
		}
		return successState;
	} 
	
	// register function
	// registers player, then, if successfull, logs player in using above function
	// returns TRUE is player successfully registered and logged in, otherwise FALSE
	protected static boolean registerPlayer(String fName, String sName, String uEmail, String password)
	{
		Socket connection = null;
		boolean successState = false;
		long startTime = System.currentTimeMillis();
		long currentTime, elapsedTime;
		try
		{
			connection = new Socket(AsxGame.connectionName, AsxGame.portNumber);
			System.out.println("Local Address: " + connection.getLocalAddress());
			System.out.println("Local Port: " + connection.getLocalPort());
			
			//Input Streams
			BufferedReader connectionRead = null;
			String response = null;
			
    		//Output Streams
			DeflaterOutputStream deflStream = null;
			
			try
			{
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
							if (login(uEmail, password)){
								successState = true;
							} 							
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
					if (elapsedTime > 10000)
					{
						System.out.println("Timeout Error!");
						successState = false;
						break;
					}
				}
			}
			catch (IOException e)
			{
				System.out.println("Exception while reading input: " + e);
				successState = false;
			}
			finally
			{
				try
				{
					connectionRead.close();
    			}
				catch (IOException e)
				{
    				System.out.println("Exception while closing streams: " +e);
    			}
			}
		}
		catch (IOException e)
		{
			System.out.println("Exception while opening connection: " + e);
			successState = false;
		}
		finally
		{
			try
			{
        		connection.close();
        	}
			catch (IOException e)
			{
        		System.out.println("Exception while closing connection: " + e);
        	}
		}
		return successState;
	}
	
	// save player function
	// saves the player currently contained in AsxGame.activePlayer
	// takes a JSONObject of transaction history to append to there file on server
	// if transHist = null, then doesn't send any
	protected static boolean saveActivePlayer(JSONObject transHist)
	{
		Socket connection = null;
		boolean successState = false;
		String saveString;
		long startTime = System.currentTimeMillis();
		long currentTime, elapsedTime;
		try
		{
			connection = new Socket(AsxGame.connectionName, AsxGame.portNumber);
			System.out.println("Local Address: " + connection.getLocalAddress());
			System.out.println("Local Port: " + connection.getLocalPort());
			
			//Input Streams
			BufferedReader connectionRead = null;
			String response = null;
			
    		//Output Streams
			DeflaterOutputStream deflStream = null;
			
			try
			{
				String emailHash = Integer.toString(AsxGame.activePlayer.email.hashCode());
				
				//register call
				deflStream = new DeflaterOutputStream(connection.getOutputStream(), true);
				System.out.println("Attempt save...");
				String playerSaveString = AsxGame.activePlayer.generateDataSaveString();
				if (transHist != null)
				{
					saveString = "save\n" + emailHash + "\n" + playerSaveString + "\n" + transHist.toString();
				}
				else
				{
					saveString = "save\n" + emailHash + "\n" + playerSaveString;
				}
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
					if (elapsedTime > 10000)
					{
						System.out.println("Timeout Error!");
						successState = false;
						break;
					}
				}
			}
			catch (IOException e)
			{
				System.out.println("Exception while reading input: " + e);
				successState = false;
			}
			finally
			{
				try
				{
					connectionRead.close();
    			}
				catch (IOException e)
				{
    				System.out.println("Exception while closing streams: " +e);
    			}
			}
		}
		catch (IOException e)
		{
			System.out.println("Exception while opening connection: " + e);
			successState = false;
		}
		finally
		{
			try
			{
        		connection.close();
        	}
			catch (IOException e)
			{
        		System.out.println("Exception while closing connection: " + e);
        	}
		}
		return successState;
	}
	
	//this function gets the response from the login request and loads the player into AsxGame.activePlayer
	//returning true if successful
	protected static boolean loadPlayer(String response)
	{
		//gets response from login function, gets values for each variable
		//and creates active player
		String transHist;
		JSONObject json = new JSONObject(response);
		String name = json.getString("Name");
		String surname = json.getString("Surname");
		String email = json.getString("Email");
		float balance = Float.parseFloat(json.getString("Balance"));
		String shareString = json.getString("Shares");
		String score = json.getString("Score");
		String rights = json.getString("Rights");
		try
		{
			transHist = json.getString("History");
		}
		catch (JSONException e)
		{
			transHist = "";
		}
		if (rights.equals("admin"))
		{
			AsxGame.activeAdmin = new Player(name,surname,email,balance,shareString,score,rights,transHist);
			AsxGame.activeAdminLoaded = true;
			return true;
		}
		else
		{
			AsxGame.activePlayer = new Player(name,surname,email,balance,shareString,score,rights,transHist);
			AsxGame.activePlayerLoaded = true;
			return true;
		}
	}
	
	// loads the leaderboard into AsxGame.leaderBoard
	// returns true if successful
	protected static boolean getValueLeaderboard()
	{
		AsxGame.leaderboard.clear();
		Socket connection = null;
		boolean successState = false;
		long startTime = System.currentTimeMillis();
		long currentTime, elapsedTime;
		try
		{
			connection = new Socket(AsxGame.connectionName, AsxGame.portNumber);
			System.out.println("Local Address: " + connection.getLocalAddress());
			System.out.println("Local Port: " + connection.getLocalPort());
			
			//Input Streams
			BufferedReader connectionRead = null;
			String response = null;
			
    		//Output Streams
			DeflaterOutputStream deflStream = null;
			
			try
			{				
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
							try
							{
								String[] leadersJSON = response.split(";");
								for (int i = 0; i < leadersJSON.length; i++)
								{
									JSONObject json = new JSONObject(leadersJSON[i]);
									AsxGame.leaderboard.add(json);
								}
								successState = true;
								break;
							}
							catch (JSONException e)
							{
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
					if (elapsedTime > 10000)
					{
						System.out.println("Timeout Error!");
						successState = false;
						break;
					}
				}
			}
			catch (IOException e)
			{
				System.out.println("Exception while reading input: " + e);
				successState = false;
			}
			finally
			{
				try
				{
					connectionRead.close();
    			}
				catch (IOException e)
				{
    				System.out.println("Exception while closing streams: " +e);
    			}
			}
		}
		catch (IOException e)
		{
			System.out.println("Exception while opening connection: " + e);
			successState = false;
		}
		finally
		{
			try
			{
        		connection.close();
        	}
			catch (IOException e)
			{
        		System.out.println("Exception while closing connection: " + e);
        	}
		}
		return successState;
	}
	
	// this function returns the current price for a specific stock
	// returns -1 if it cant find a stock
	public static float getStockCurrentPrice(String asxCode)
	{
		for (int i = 0; i < AsxGame.stockArray.size(); i++)
		{
			if (AsxGame.stockArray.get(i).code.equals(asxCode))
			{
				return AsxGame.stockArray.get(i).askPrice;
			}
		}
		return -1;
	}
	
	public static boolean getStockHistory(String asxCode, int startDate, int endDate) //int date = 20170214 -- 14th Feb 2017
	{
		AsxGame.requestedStockCode = asxCode;
		AsxGame.requestedStockHistory.clear();
		Socket connection = null;
		boolean successState = false;
		try
		{
			connection = new Socket(AsxGame.connectionName, AsxGame.portNumber);
			
			//Input Streams
			BufferedReader connectionRead = null;
			String response = null;
			
    		//Output Streams
			DeflaterOutputStream deflStream = null;
			
			try
			{
				// call
				deflStream = new DeflaterOutputStream(connection.getOutputStream(), true);
				System.out.println("Attempt getStockHistory...");
				String sendString = "stockHistory\n"+asxCode+"\n"+startDate+"\n"+endDate;
				byte[] sendBytes = sendString.getBytes("UTF-8");
				deflStream.write(sendBytes);
				deflStream.finish();
				deflStream.flush();
				while (true)
				{
					connectionRead = new BufferedReader(new InputStreamReader(new InflaterInputStream(connection.getInputStream())));
					response = connectionRead.readLine();
					if(response != null)
					{
						if(response.equals("200"))
						{
							successState = true;
							while((response = connectionRead.readLine()) != null)
							{
								JSONObject stockHis = new JSONObject(response);
								AsxGame.requestedStockHistory.add(stockHis);
							}
							connectionRead.close();
							break;
						}
						else
						{
							System.out.println("500: INTERNAL SERVER ERROR!");
							successState = false;
							break;
						}
					}
				}
				
			} 
			catch (IOException e) 
			{
				System.out.println("Exception while communicating with server: " + e);
				successState = false;
			}
		} 
		catch (IOException e) 
		{
			System.out.println("Exception while opening connection: " + e);
			successState = false;
		}
		return successState;
	}
	
	//these 2 used to calculate the brokers fee for buying and selling
	// both return floats
	public static float calcBrokersFeePurch(float transactionAmount)
	{		
		Socket connection = null;
		//default values if server can't be reached
		float flat = 50;
		float percentage = 1;
		try{
			connection = new Socket(AsxGame.connectionName, AsxGame.portNumber);
			
			//Input Streams
			BufferedReader connectionRead = null;
			String response = null;
			
    		//Output Streams
			DeflaterOutputStream deflStream = null;
			
			try
			{
				// call
				deflStream = new DeflaterOutputStream(connection.getOutputStream(), true);
				System.out.println("Attempt getBuy...");
				String sendString = "getBuy\n";
				byte[] sendBytes = sendString.getBytes("UTF-8");
				deflStream.write(sendBytes);
				deflStream.finish();
				deflStream.flush();
				
				while (true)
				{
					connectionRead = new BufferedReader(new InputStreamReader(new InflaterInputStream(connection.getInputStream())));
					response = connectionRead.readLine();
					if(response != null)
					{
						if(!response.equals("500"))
						{
							flat = Float.parseFloat(connectionRead.readLine());
							percentage = Float.parseFloat(connectionRead.readLine());
							break;
						}
						else
						{
							System.out.println("500: INTERNAL SERVER ERROR!");
							break;
						}
					}
				}
				
			}
			catch (IOException e)
			{
				System.out.println("Exception while reading connection response: " + e);
			}
		}
		catch (IOException e)
		{
			System.out.println("Exception while opening connection: " + e);
		}
		try
		{
			connection.close();
		}
		catch (IOException e)
		{
			System.out.println("Exception while closing connection: " + e);
		}
		
		float purchaseFee = transactionAmount * percentage/100;
		purchaseFee += flat;
		return purchaseFee;
	}
	
	public static float calcBrokersFeeSale(float transactionAmount)
	{ 	//$50 + %0.25
		/*float output = 50;
		float fee = (float) (transactionAmount * 0.0025);
		output += fee;
		return output;*/
		
		Socket connection = null;
		//default values if server can't be reached
		float flat = 50;
		float percentage = 0.25f;
		try{
			connection = new Socket(AsxGame.connectionName, AsxGame.portNumber);
			
			//Input Streams
			BufferedReader connectionRead = null;
			String response = null;
			
    		//Output Streams
			DeflaterOutputStream deflStream = null;
			
			try
			{
				// call
				deflStream = new DeflaterOutputStream(connection.getOutputStream(), true);
				System.out.println("Attempt getSell...");
				String sendString = "getSell\n";
				byte[] sendBytes = sendString.getBytes("UTF-8");
				deflStream.write(sendBytes);
				deflStream.finish();
				deflStream.flush();
				
				while (true)
				{
					connectionRead = new BufferedReader(new InputStreamReader(new InflaterInputStream(connection.getInputStream())));
					response = connectionRead.readLine();
					if(response != null)
					{
						if(!response.equals("500"))
						{
							flat = Float.parseFloat(connectionRead.readLine());
							percentage = Float.parseFloat(connectionRead.readLine());
							break;
						}
						else
						{
							System.out.println("500: INTERNAL SERVER ERROR!");
							break;
						}
					}
				}
				
			}
			catch (IOException e)
			{
				System.out.println("Exception while reading connection response: " + e);
			}
		}
		catch (IOException e)
		{
			System.out.println("Exception while opening connection: " + e);
		}
		try
		{
			connection.close();
		}
		catch (IOException e)
		{
			System.out.println("Exception while closing connection: " + e);
		}
		
		float saleFee = transactionAmount * percentage/100;
		saleFee += flat;
		return saleFee;
	}
}

