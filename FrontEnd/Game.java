/*
	Main game controls are here
	Including:
		- Buying and Selling functions
		- login,register and save functions
		- leaderBoard download function
		- get current price for specified stock function
	
 */

package com.amazonaws.samples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.json.*;

public class Game
{
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
	
	protected static boolean login(String uEmail, String password)
	{
		boolean successState = false;
		String emailHash = Integer.toString(uEmail.hashCode());
		String pwHash = Integer.toString(password.hashCode());
		System.out.println("Attempt login...");
		String sendString = "login\n" + emailHash + "\n" + pwHash;
		String response = Utilities.sendServerMessage(sendString);
		ArrayList<String> lines = new ArrayList<String>(Arrays.asList(response.split("\n")));
		if(!lines.get(0).equals("401"))
		{
			loadPlayer(lines.get(0));
			boolean trans = false;
			boolean val = false;
			for(String line:lines)
			{
				if(line.equals("transaction"))
				{
					trans = true;
				}
				else if(line.equals("value"))
				{
					trans = false;
					val = true;
				}
				else if(trans)
				{
					JSONObject histLine = new JSONObject(line);
					if(AsxGame.activePlayerLoaded)
					{
						AsxGame.activePlayer.transHistory.add(histLine);
					}
					else if(AsxGame.activeAdminLoaded)
					{
						AsxGame.activeAdmin.transHistory.add(histLine);
					}
				}
				else if(val)
				{
					JSONObject valLine = new JSONObject(line);
					if(AsxGame.activePlayerLoaded)
					{
						AsxGame.activePlayer.valueHistory.add(valLine);
					}
					else if(AsxGame.activeAdminLoaded)
					{
						AsxGame.activeAdmin.valueHistory.add(valLine);
					}
				}
			}
			if(AsxGame.activePlayerLoaded)
			{
				AsxGame.activePlayer.messages = getMessageList();
				AsxGame.activePlayer.unreadMessages = getUnreadMessages();
				AsxGame.activePlayer.deletedMessages = getDeletedMessageList();
				AsxGame.activePlayer.pendingFunds = getFundsList();
			}
			else if(AsxGame.activeAdminLoaded)
			{
				AsxGame.activeAdmin.messages = getMessageList();
				AsxGame.activeAdmin.unreadMessages = getUnreadMessages();
				AsxGame.activeAdmin.deletedMessages = getDeletedMessageList();
			}
			AsxGame.messageChecker = new Thread(new MessageCheck());
			AsxGame.messageChecker.start();
			successState = true;
		}
		else
		{
			System.out.println("401: UNAUTHORIZED!");
			successState = false;
		}
		
		return successState;
	}
	
	protected static void logout()
	{
		if(AsxGame.activePlayerLoaded)
		{
			AsxGame.messageChecker.interrupt();
			AsxGame.activePlayer = null;
			AsxGame.activePlayerLoaded = false;
		}
		else if(AsxGame.activeAdminLoaded)
		{
			AsxGame.messageChecker.interrupt();
			AsxGame.activeAdminLoaded = false;
			AsxGame.activeAdmin = null;
			AsxGame.activePlayer = null;
			AsxGame.activePlayerLoaded = false;
		}
	}
	
	protected static boolean registerPlayer(String fName, String sName, String uEmail, String password)
	{
		boolean successState = false;
		String pwHash = Integer.toString(password.hashCode());
		System.out.println("Attempt registration...");
		String sendString = "register\n" + pwHash + "\n" + fName + "\n" + sName + "\n" + uEmail;
		System.out.println(sendString);
		String response = Utilities.sendServerMessage(sendString);
		if(!response.equals("500\n"))
		{
			System.out.println("Registration successful!");
			if (login(uEmail, password))
			{
				successState = true;
			} 							
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
		}
		return successState;
	}
	

	protected static boolean saveActivePlayer(JSONObject transHist)
	{
		boolean successState = false;
		String saveString;
		System.out.println("Attempt save...");
		String emailHash = Integer.toString(AsxGame.activePlayer.email.hashCode());
		String playerSaveString = AsxGame.activePlayer.generateDataSaveString();
		if (transHist != null)
		{
			saveString = "save\n" + emailHash + "\n" + playerSaveString + "\n" + transHist.toString();
		}
		else
		{
			saveString = "save\n" + emailHash + "\n" + playerSaveString;
		}
		String response = Utilities.sendServerMessage(saveString);
		if(!response.equals("500\n"))
		{
			System.out.println("Save Successful!");
			successState = true;
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
		}
		return successState;
	}
	

	protected static boolean loadPlayer(String response)
	{
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
		if (rights.equals("admin"))
		{
			AsxGame.activeAdmin = new Player(name,surname,email,balance,shareString,score,rights);
			AsxGame.activeAdminLoaded = true;
			return true;
		}
		else
		{
			AsxGame.activePlayer = new Player(name,surname,email,balance,shareString,score,rights);
			AsxGame.activePlayerLoaded = true;
			return true;
		}
	}
	
	protected static boolean getValueLeaderboard()
	{
		AsxGame.leaderboard.clear();
		boolean successState = false;
		System.out.println("Attempt leaders...");
		String sendString = "leaders\n" + "0" + "\n" + "10"; //'0' is top position returned, '10' is number of places returned
		String response = Utilities.sendServerMessage(sendString);
		if(!response.equals("500\n"))
		{
			String[] leadersJSON = response.split(";");
			for (int i = 0; i < leadersJSON.length; i++)
			{
				JSONObject json = new JSONObject(leadersJSON[i]);
				AsxGame.leaderboard.add(json);
			}
			successState = true;
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
		}
		return successState;
	}
	
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
	
	public static String getStockName(String asxCode){
		for (int i = 0; i < AsxGame.stockArray.size(); i++){
			if (AsxGame.stockArray.get(i).code.equals(asxCode)){
				return AsxGame.stockArray.get(i).name;
			}
		}
		return null;
	}
	
	public static boolean getStockHistory(String asxCode, int startDate, int endDate) //int date = 20170214 -- 14th Feb 2017
	{
		AsxGame.requestedStockCode = asxCode;
		AsxGame.requestedStockHistory.clear();
		boolean successState = false;
		System.out.println("Attempt getStockHistory...");
		String sendString = "stockHistory\n"+asxCode+"\n"+startDate+"\n"+endDate;
		String response = Utilities.sendServerMessage(sendString);
		ArrayList<String> lines = new ArrayList<String>(Arrays.asList(response.split("\n")));
		if(lines.get(0).equals("200"))
		{
			successState = true;
			for(String line:lines)
			{
				if(!line.equals("200"))
				{
					JSONObject stockHis = new JSONObject(line);
					AsxGame.requestedStockHistory.add(stockHis);
				}
			}
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
			successState = false;
		}
		return successState;
	}
	
	public static float calcBrokersFeePurch(float transactionAmount)
	{
		//default values if server can't be reached
		float flat = 50;
		float percentage = 1;
		System.out.println("Attempt getBuy...");
		String sendString = "getBuy\n";
		String response = Utilities.sendServerMessage(sendString);
		ArrayList<String> lines = new ArrayList<String>(Arrays.asList(response.split("\n")));
		if(!lines.get(0).equals("500"))
		{
			flat = Float.parseFloat(lines.get(1).toString());
			percentage = Float.parseFloat(lines.get(2).toString());
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
		}
		
		float purchaseFee = transactionAmount * percentage/100;
		purchaseFee += flat;
		return purchaseFee;
	}
	
	public static float calcBrokersFeeSale(float transactionAmount)
	{ 
		//default values if server can't be reached
		float flat = 50;
		float percentage = 0.25f;
		System.out.println("Attempt getSell...");
		String sendString = "getSell\n";
		String response = Utilities.sendServerMessage(sendString);
		ArrayList<String> lines = new ArrayList<String>(Arrays.asList(response.split("\n")));
		if(!lines.get(0).equals("500"))
		{
			flat = Float.parseFloat(lines.get(1).toString());
			percentage = Float.parseFloat(lines.get(2).toString());
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
		}
		
		float saleFee = transactionAmount * percentage/100;
		saleFee += flat;
		return saleFee;
	}
	
	public static boolean sendMessage(String sender, String recipient, String subject, String message)
	{
		//convert currentPlayer email and recipient email to hash
		boolean state = false;
		String senderHash = "";
		if(sender == null)
		{
			senderHash = Integer.toString(AsxGame.activePlayer.email.hashCode());
		}
		else
		{
			senderHash = Integer.toString(sender.hashCode());
		}
		String recipientHash = Integer.toString(recipient.hashCode());
		System.out.println("Attempt sendMessage...");
		String sendString = "sendMessage\n"+senderHash+"\n"+recipientHash+"\n"+subject+"\n"+message+"\n";
		String response = Utilities.sendServerMessage(sendString);
		if(response.equals("200\n"))
		{
			System.out.println("200");
			state = true;
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
			state = false;
		}
		return state;
	}
	
	public static ArrayList<Integer> getMessageList()
	{
		//convert currentPlayer email and recipient email to hash
		String userHash = "";
		if(AsxGame.activePlayerLoaded)
		{
			userHash = Integer.toString(AsxGame.activePlayer.email.hashCode());
		}
		else if(AsxGame.activeAdminLoaded)
		{
			userHash = Integer.toString(AsxGame.activeAdmin.email.hashCode());
		}
		else
		{
			return null;
		}
		String messageList = "";
		System.out.println("Attempt getMessageList...");
		String sendString = "getMessageList\n"+userHash;
		String response = Utilities.sendServerMessage(sendString);
		if(!response.equals("500\n"))
		{
			messageList = response.toString();
			if(response.equals("204\n"))
			{
				System.out.println("Mailbox Empty!");
			}
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
			return null;
		}
		String[] sList = messageList.split("[,\n]");
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(String s:sList)
		{
			list.add(Integer.parseInt(s));
		}
		return list;
	}
	
	public static ArrayList<Integer> getDeletedMessageList()
	{
		//convert currentPlayer email and recipient email to hash
		String userHash = "";
		if(AsxGame.activePlayerLoaded)
		{
			userHash = Integer.toString(AsxGame.activePlayer.email.hashCode());
		}
		else if(AsxGame.activeAdminLoaded)
		{
			userHash = Integer.toString(AsxGame.activeAdmin.email.hashCode());
		}
		else
		{
			return null;
		}
		String messageList = "";
		System.out.println("Attempt getDeleted...");
		String sendString = "getDeleted\n"+userHash;
		String response = Utilities.sendServerMessage(sendString);
		if(!response.equals("500\n"))
		{
			messageList = response.toString();
			if(response.equals("204\n"))
			{
				System.out.println("No deleted messages!");
			}
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
			return null;
		}
		String[] sList = messageList.split("[,\n]");
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(String s:sList)
		{
			list.add(Integer.parseInt(s));
		}
		return list;
	}
	
	public static String getMessage(int messageID)
	{
		//convert currentPlayer email and recipient email to hash
		String userHash = "";
		if(AsxGame.activePlayerLoaded)
		{
			userHash = Integer.toString(AsxGame.activePlayer.email.hashCode());
		}
		else if(AsxGame.activeAdminLoaded)
		{
			userHash = Integer.toString(AsxGame.activeAdmin.email.hashCode());
		}
		else
		{
			return null;
		}
		String message = "";
		System.out.println("Attempt getMessage...");
		String sendString = "getMessage\n"+userHash+"\n"+messageID;
		String response = Utilities.sendServerMessage(sendString);
		if(!response.equals("500\n"))
		{
			message = response;
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
			return null;
		}
		return message;
	}
	
	public static ArrayList<Integer> getUnreadMessages()
	{
		//convert currentPlayer email and recipient email to hash
		String userHash = "";
		if(AsxGame.activePlayerLoaded)
		{
			userHash = Integer.toString(AsxGame.activePlayer.email.hashCode());
		}
		else if(AsxGame.activeAdminLoaded)
		{
			userHash = Integer.toString(AsxGame.activeAdmin.email.hashCode());
		}
		else
		{
			return null;
		}
		String messageList = "";
		System.out.println("Attempt getUnreadMessages...");
		String sendString = "unreadMail\n"+userHash;
		String response = Utilities.sendServerMessage(sendString);
		if(!response.equals("500\n"))
		{
			if(response.equals("0\n"))
			{
				messageList = null;
			}
			messageList = response;
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
			return null;
		}
		String[] sList = messageList.split("[,\n]");
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(String s:sList)
		{
			list.add(Integer.parseInt(s));
		}
		return list;
	}
	
	public static boolean deleteMessage(int messageID)
	{
		//convert currentPlayer email and recipient email to hash
		String userHash = "";
		if(AsxGame.activePlayerLoaded)
		{
			userHash = Integer.toString(AsxGame.activePlayer.email.hashCode());
		}
		else if(AsxGame.activeAdminLoaded)
		{
			userHash = Integer.toString(AsxGame.activeAdmin.email.hashCode());
		}
		else
		{
			return false;
		}
		boolean state = false;
		System.out.println("Attempt deleteMessage...");
		String sendString = "deleteMessage\n"+userHash+"\n"+messageID+"\n";
		String response = Utilities.sendServerMessage(sendString);
		if(response.equals("200\n"))
		{
			System.out.println("200");
			state = true;
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
			state = false;
		}
		return state;
	}
	
	public static boolean markUnread(int messageID)
	{
		//convert currentPlayer email and recipient email to hash
		String userHash = "";
		if(AsxGame.activePlayerLoaded)
		{
			userHash = Integer.toString(AsxGame.activePlayer.email.hashCode());
		}
		else if(AsxGame.activeAdminLoaded)
		{
			userHash = Integer.toString(AsxGame.activeAdmin.email.hashCode());
		}
		else
		{
			return false;
		}
		boolean state = false;
		System.out.println("Attempt markUnread...");
		String sendString = "markUnread\n"+userHash+"\n"+messageID+"\n";
		String response = Utilities.sendServerMessage(sendString);
		if(response.equals("200\n"))
		{
			System.out.println("200");
			state = true;
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
			state = false;
		}
		return state;
	}
	
	public static boolean sendFunds(String recipient, float amount)
	{
		if(AsxGame.activePlayer.balance > amount)
		{
			String userHash = Integer.toString(AsxGame.activePlayer.email.hashCode());
			boolean state = false;
			String recipientHash = Integer.toString(recipient.hashCode());
			System.out.println("Attempt sendFunds...");
			String sendString = "sendFunds\n"+userHash+"\n"+recipientHash+"\n"+amount+"\n";
			String response = Utilities.sendServerMessage(sendString);
			if(response.equals("200\n"))
			{
				System.out.println("200");
				AsxGame.activePlayer.removeBalance(amount);
				state = true;
			}
			else
			{
				System.out.println("500: INTERNAL SERVER ERROR!");
				state = false;
			}
			return state;
		}
		else
		{
			return false;
		}
	}
	
	public static boolean acceptFunds(String fundID, float amount)
	{
		String userHash = Integer.toString(AsxGame.activePlayer.email.hashCode());
		boolean state = false;
		System.out.println("Attempt acceptFunds...");
		String sendString = "acceptFunds\n"+userHash+"\n"+fundID+"\n"+amount+"\n";
		String response = Utilities.sendServerMessage(sendString);
		if(response.equals("200\n"))
		{
			System.out.println("200");
			AsxGame.activePlayer.addBalance(amount);
			state = true;
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
			state = false;
		}
		return state;
	}
	
	public static ArrayList<Integer> getFundsList()
	{
		//convert currentPlayer email and recipient email to hash
		String userHash = Integer.toString(AsxGame.activePlayer.email.hashCode());
		String messageList = "";
		System.out.println("Attempt getFundsList...");
		String sendString = "getFundsList\n"+userHash;
		String response = Utilities.sendServerMessage(sendString);
		if(!response.equals("500\n"))
		{
			messageList = response.toString();
			if(response.equals("204\n"))
			{
				System.out.println("No pending funds trasnfers!");
			}
		}
		else
		{
			System.out.println("500: INTERNAL SERVER ERROR!");
			return null;
		}
		String[] sList = messageList.split("[,\n]");
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(String s:sList)
		{
			list.add(Integer.parseInt(s));
		}
		return list;
	}
	

	public static boolean playerDeleteSelf()
	{
		boolean successState = false;
		Scanner consoleRead = new Scanner(System.in);
		String uEmail, password, conf;
		System.out.println("Please enter username/email address: ");
		uEmail = consoleRead.next();
		if(!uEmail.equals(AsxGame.activePlayer.email))
		{
			System.out.println("Unauthorised action! Email address incorrect!");
			consoleRead.close();
			return false;
		}
		System.out.println("Please enter password: ");
		password = consoleRead.next();
		String emailHash = Integer.toString(uEmail.hashCode());
		String pwHash = Integer.toString(password.hashCode());
		System.out.println("Attempt login...");
		String sendString = "login\n" + emailHash + "\n" + pwHash;
		String response = Utilities.sendServerMessage(sendString);
		ArrayList<String> lines = new ArrayList<String>(Arrays.asList(response.split("\n")));
		if(!lines.get(0).equals("401"))
		{
			successState = true;
		}
		else
		{
			System.out.println("401: UNAUTHORIZED!");
			successState = false;
		}
		System.out.println("Are you sure you want to delete your account?");
		System.out.println("Enter y to continue.");
		conf = consoleRead.next();
		consoleRead.close();
		if(conf.equalsIgnoreCase("y"))
		{
			System.out.println("Attempt deleteAccount...");
			sendString = "deleteAccount\n"+emailHash;
			response = Utilities.sendServerMessage(sendString);
			if(response.equals("200\n"))
			{
				System.out.println("User: " + uEmail + " deleted!");
				successState = true;
			}
			else
			{
				System.out.println("500: INTERNAL SERVER ERROR!");
				successState = false;
			}
		}
		else
		{
			successState = false;
		}
		return successState;
	}
}

