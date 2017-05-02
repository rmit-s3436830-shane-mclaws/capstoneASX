/*
 	Admin class
 	
 	All admin functionality should be placed here
 	needs work
  
 */

package com.amazonaws.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.json.JSONObject;

public class Admin
{
	private static ArrayList<String> playerList = new ArrayList<String>();
	
	//returns the private variable playerList
	protected static ArrayList<String> returnPlayerList()
	{
		return playerList;
	}
	
	protected static void adminUnloadPlayer()
	{
		//Save then unload
		Game.saveActivePlayer(null);
		AsxGame.activePlayer = null;
		AsxGame.activePlayerLoaded = false;
	}
	
	//gets a list of all users
	//I do not know if this works, I have not tested it - Shane
	protected static boolean getUserList()
	{
		Socket connection = null;
		boolean successState = false;
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
				System.out.println("Attempt getUserList...");
				String sendString = "getUser\n*";
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
							while (response != null)
							{
								playerList.add(response);
								response = connectionRead.readLine();
							}
							successState = true;
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
				System.out.println("Exception while reading connection response: " + e);
			}
		}
		catch (IOException e)
		{
			System.out.println("Exception while opening connection: " + e);
			successState = false;
		}
		try
		{
			connection.close();
		}
		catch (IOException e)
		{
			System.out.println("Exception while closing connection: " + e);
			successState = false;
		}
		
		return successState;
	}
	
	//Gets a users data String from the server - Cal
	//user is the desired users email address
	protected static void adminLoadPlayer(String user)
	{
		String emailHash = Integer.toString(user.hashCode());
		Socket connection = null;
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
				System.out.println("Attempt getUser...");
				String sendString = "getUser\n"+emailHash;
				byte[] sendBytes = sendString.getBytes("UTF-8");
				deflStream.write(sendBytes);
				deflStream.finish();
				deflStream.flush();
				while (true)
				{
					connectionRead = new BufferedReader(new InputStreamReader(new InflaterInputStream(connection.getInputStream())));
					response = connectionRead.readLine();
					System.out.println(response);
					if(response != null)
					{
						if(!response.equals("500"))
						{
							Game.loadPlayer(response);
							while((response = connectionRead.readLine()) != null)
							{
								JSONObject histLine = new JSONObject(response);
								AsxGame.activePlayer.transHistory.add(histLine);
							}
							connectionRead.close();
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
				System.out.println("Exception while communicating with server: " + e);
			}
		} 
		catch (IOException e) 
		{
			System.out.println("Exception while opening connection: " + e);
		}
		return;
	}
	
	protected static boolean changeBalance(float newBalance)
	{
		//Changes the balance of the loaded player
		if(AsxGame.activePlayer != null)
		{
			AsxGame.activePlayer.balance = newBalance;
			Game.saveActivePlayer(null);
			return true;
		}
		//No player loaded in
		return false;
	}
	
	protected static boolean addStock(String asxCode, int number)
	{
		//Adds a quantity of stock to the loaded player
		if(AsxGame.activePlayer != null)
		{
			ArrayList<String> shareList= AsxGame.activePlayer.shares;
			int count = 0;
			for(String shareString:shareList)
			{
				String share = shareString.split(":")[0];
				int qty = Integer.parseInt(shareString.split(":")[1]);
				if(share.equals(asxCode)) //If user already owns a quantity in the share being added
				{
					qty += number;
					shareString = share + ":" + qty;
					shareList.set(count, shareString); //Replace old share data with new, updated share data
					AsxGame.activePlayer.shares = shareList;
					Game.saveActivePlayer(null);
					return true;
				}
				count++;
			}
			//If code reaches this point, the user doesn't own any of the shares that are being added
			String shareString = asxCode + ":" + number;
			shareList.add(shareString);
			Game.saveActivePlayer(null);
			return true;
		}
		//No player loaded in
		return false;
	}
	
	protected static boolean removeStock(String asxCode, int number)
	{
		//Removes a quantity of stocks from the loaded player
		if(AsxGame.activePlayer != null)
		{
			ArrayList<String> shareList= AsxGame.activePlayer.shares;
			int count = 0;
			for(String shareString:shareList)
			{
				String share = shareString.split(":")[0];
				int qty = Integer.parseInt(shareString.split(":")[1]);
				if(share.equals(asxCode)) //If user already owns a quantity in the share being added
				{
					qty -= number;
					if(qty <= 0)
					{
						//User now owns 0 of stock being removed
						shareList.remove(count);
						AsxGame.activePlayer.shares = shareList;
						Game.saveActivePlayer(null);
						return true;
					}
					else
					{
						shareString = share + ":" + qty;
						shareList.set(count, shareString); //Replace old share data with new, updated share data
						AsxGame.activePlayer.shares = shareList;
						Game.saveActivePlayer(null);
						return true;
					}
				}
				count++;
			}
			//If code reaches this point, the user doesn't own any of the shares that are being removed
			return false;
		}
		//No player loaded in
		return false;
	}
	
	protected static boolean setBuyFee(float flat, float percentage)
	{
		boolean successState = false;
		Socket connection = null;
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
				System.out.println("Attempt setBuy...");
				String sendString = "setBuy\n"+flat+"\n"+percentage;
				byte[] sendBytes = sendString.getBytes("UTF-8");
				deflStream.write(sendBytes);
				deflStream.finish();
				deflStream.flush();
				while (true)
				{
					connectionRead = new BufferedReader(new InputStreamReader(new InflaterInputStream(connection.getInputStream())));
					response = connectionRead.readLine();
					System.out.println(response);
					if(response != null)
					{
						if(response.equals("200"))
						{
							successState = true;
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
			}
		} 
		catch (IOException e) 
		{
			System.out.println("Exception while opening connection: " + e);
		}
		return successState;
	}
	
	protected static boolean setSellFee(float flat, float percentage)
	{
		boolean successState = false;
		Socket connection = null;
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
				System.out.println("Attempt setSell...");
				String sendString = "setSell\n"+flat+"\n"+percentage;
				byte[] sendBytes = sendString.getBytes("UTF-8");
				deflStream.write(sendBytes);
				deflStream.finish();
				deflStream.flush();
				while (true)
				{
					connectionRead = new BufferedReader(new InputStreamReader(new InflaterInputStream(connection.getInputStream())));
					response = connectionRead.readLine();
					System.out.println(response);
					if(response != null)
					{
						if(response.equals("200"))
						{
							successState = true;
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
			}
		} 
		catch (IOException e) 
		{
			System.out.println("Exception while opening connection: " + e);
		}
		return successState;
	}
	
}
