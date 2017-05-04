/*
 	Admin class
 	
 	All admin functionality should be placed here
 	needs work
  
 */

package com.amazonaws.samples;

import java.util.ArrayList;
import java.util.Arrays;

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
		boolean successState = false;
		System.out.println("Attempt getUserList...");
		String sendString = "getUser\n*";
		String response = Utilities.sendServerMessage(sendString);
		ArrayList<String> lines = new ArrayList<String>(Arrays.asList(response.split("\n")));
		if(!lines.get(0).equals("500"))
		{
			for(String line:lines)
			{
				playerList.add(line);
			}
			successState = true;
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
			successState = false;
		}
		return successState;
	}
	
	//Gets a users data String from the server - Cal
	//user is the desired users email address
	protected static void adminLoadPlayer(String user)
	{
		String emailHash = Integer.toString(user.hashCode());
		System.out.println("Attempt getUser...");
		String sendString = "getUser\n"+emailHash;
		String response = Utilities.sendServerMessage(sendString);
		ArrayList<String> lines = new ArrayList<String>(Arrays.asList(response.split("\n")));
		if(!lines.get(0).equals("500"))
		{
			Game.loadPlayer(lines.get(0));
			for(String line:lines)
			{
				if(!line.equals(lines.get(0)))
				{
					JSONObject histLine = new JSONObject(line);
					AsxGame.activePlayer.transHistory.add(histLine);
				}
			}
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
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
		System.out.println("Attempt setBuy...");
		String sendString = "setBuy\n"+flat+"\n"+percentage;
		String response = Utilities.sendServerMessage(sendString);
		if(response.equals("200\n"))
		{
			successState = true;
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
			successState = false;
		}
		return successState;
	}
	
	protected static boolean setSellFee(float flat, float percentage)
	{
		boolean successState = false;
		System.out.println("Attempt setSell...");
		String sendString = "setSell\n"+flat+"\n"+percentage;
		String response = Utilities.sendServerMessage(sendString);
		if(response.equals("200\n"))
		{
			successState = true;
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
			successState = false;
		}
		return successState;
	}
	
	protected static void messageAllUsers(String type, String subject, String message)
	{
		if(playerList.isEmpty())
		{
			getUserList();
		}
		for(String player:playerList)
		{
			JSONObject playerJSON = new JSONObject(player);
			String email = playerJSON.getString("Email");
			System.out.println("Emailing: " + email);
			Game.sendMessage(AsxGame.activeAdmin.email, email, type, subject, message);
		}
	}
	
}
