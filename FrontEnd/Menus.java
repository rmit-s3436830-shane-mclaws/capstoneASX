package com.amazonaws.samples;

import java.util.*;

import org.json.JSONObject;

//temp control file while the project is console operated
public class Menus
{	
	private static Scanner consoleRead = new Scanner(System.in);
	private static String input; 
	private static int inputInt;
	private static int qty;
	private static float amount;
	
	protected static void menuLogin()
	{		
		System.out.println("\nPlease select on of the following options: \n");
		System.out.println("	1. Login with existing account");
		System.out.println("	2. Register new player");
		System.out.println("	3. Get temp asx data + print");
		System.out.println("	0. Exit");
		input = consoleRead.next();
		inputInt = Integer.parseInt(input);
		switch (inputInt)
		{
			case 1:
				menuLoginDialogue();
				return;
			case 2:
				menuRegisterDialogue();
				return;
			case 3:
				//Utilities.loadTempStockList();
				AsxPull.getAsxJson("ABP", "20170329");
				AsxGame.stockArray.get(0).printStock();
				AsxGame.stockArray.remove(0);
				return;
			case 0: 		//exit
				System.exit(0);
				break;
		}
	}
	
	protected static void menuLoginDialogue()
	{
		String uEmail, password;
		System.out.println("Please enter username/email address: ");
		uEmail = consoleRead.next();
		System.out.println("Please enter password: ");
		password = consoleRead.next();
		Game.login(uEmail, password);
	}
	
	protected static void menuRegisterDialogue()
	{
		String name, surname, email, emailCheck, password, pwCheck;
		boolean emailCorrect = false;
		boolean passCorrect = false;
		boolean allCorrect = false;
		do 
		{
			System.out.println("Please enter first name: ");
			name = consoleRead.next();
			System.out.println("Please enter surname: ");
			surname = consoleRead.next();
			do 
			{
				System.out.println("Please enter email address: ");
				email = consoleRead.next();
				System.out.println("Please re-enter email address: ");
				emailCheck = consoleRead.next();
				if (email.equals(emailCheck))
				{
					emailCorrect = true;
				} 
				else 
				{
					System.out.println("Email adresses are different, please try again!");
				}			
			} while (emailCorrect == false);
			do 
			{
				System.out.println("Please enter pasasword: ");
				password = consoleRead.next();
				System.out.println("Please re-enter password: ");
				pwCheck = consoleRead.next();
				if (password.equals(pwCheck))
				{
					passCorrect = true;
				}
				else
				{
					System.out.println("passwords are different, please try again!");
				}			
			} while (passCorrect == false);
			System.out.println("Your entered details are: ");
			System.out.println("Name: " + name + " " + surname);
			System.out.println("Email: " + email);
			System.out.println("Is this correct? Y/N");
			input  = consoleRead.next();
			System.out.println(input);
			if (input.equals("y") || input.equals("Y"))
			{
				System.out.println("Attempting to register player");
				Game.registerPlayer(name, surname, email, password);
				allCorrect = true;
			}
		} while (allCorrect == false);
		
		
	}
	
	protected static void buyStockDialogue()
	{				//all dialogue for buying stocks
		if (AsxGame.stockArray.size() != 0)
		{
			System.out.println("All available stocks: ");
			for (int i = 0; i < AsxGame.stockArray.size(); i++)
			{
				AsxGame.stockArray.get(i).printStock();
			}
			System.out.println("Please select a stock to buy (give code, case sensitive)");
			input = consoleRead.next();
			for (int index = 0; index < AsxGame.stockArray.size(); index++)
			{
				if (input.equals(AsxGame.stockArray.get(index).code))
				{
					System.out.println("Purchasing stock :" + AsxGame.stockArray.get(index).name);
					System.out.println("Enter number you wish to purchase");
					String input2 = consoleRead.next();
					inputInt = Integer.parseInt(input2);
					boolean success = Game.buyStocks(input, inputInt);
					if (success == true)
					{
						System.out.println("Shares purchased");
					}
					else if (success == false)
					{
						System.out.println("Shares NOT purchased");
					}
				}
			}
		}
	}
	
	protected static void sellStockDialogue()
	{					//all dialogue for selling stocks
		if (AsxGame.stockArray.size() != 0)
		{
			System.out.println("Players stocks: ");
			AsxGame.activePlayer.printShares();
			System.out.println("Please select a stock to sell (give code, case sensitive)");
			input = consoleRead.next();
			for (int index = 0; index < AsxGame.activePlayer.shares.size(); index++)
			{
				String[] shareSplit = AsxGame.activePlayer.shares.get(index).split(":");
				if (input.equals(shareSplit[0]))
				{
					System.out.println("Selling stock :" + shareSplit[0]);
					System.out.println("Enter number you wish to sell");
					input = consoleRead.next();
					inputInt = Integer.parseInt(input);
					if (Game.sellStocks(shareSplit[0], inputInt))
					{
						System.out.println("Shares Sold");
					}
				}
			}
		}
	}
	
	protected static void mainMenu()
	{
		//Utilities.loadTempStockList();
		while (true)
		{		
			System.out.println("\nPlease select on of the following options: \n");
			System.out.println("	1. Print Active Player Details");
			System.out.println("	2. List all availble stocks");
			System.out.println("	3. Buy stocks");
			System.out.println("	4. Sell stocks");
			System.out.println("	5. Save player to server");
			System.out.println("	6. Get Leaderboard");
	//		System.out.println("	L. Load stock list (only a select few stocks available currently");
			System.out.println("	7. Send Message");
			System.out.println("	8. View a message");
			System.out.println("	9. Delete a message");
			System.out.println("	10. Logout (NOTE: THIS DOES NOT SAVE YOUR PLAYER CURRENTLY!");
			System.out.println("	0. Exit");
			
			input = consoleRead.next();
			inputInt = Integer.parseInt(input);
			switch (inputInt) 
			{
				case 1:		//Print Active Player Details
					AsxGame.activePlayer.printPlayer();
					break;
				case 2: 		//List all available stocks
					System.out.println(AsxGame.stockArray.size());
					for (int i = 0; i < AsxGame.stockArray.size(); i++)
					{
						AsxGame.stockArray.get(i).printStock();
					}
					break;
				case 3:		//Buy stocks
					buyStockDialogue();
					break;
				case 4:		//Sell Stocks
					sellStockDialogue();
					break;
				case 5:		//Save player to server
					//System.out.println(AsxGame.activePlayer.generateSaveString());
					Game.saveActivePlayer(null);
					break;
				case 6:
					Game.getValueLeaderboard();
					for (int i = 0; i < AsxGame.leaderboard.size(); i++)
					{
						System.out.println(AsxGame.leaderboard.get(i));
					}
					break;
	//			case "L":
	//				Utilities.loadTempStockList();
	//				break;
				case 7: //Send Message
					System.out.println("Enter recipient then message");
					String recipient = consoleRead.next();
					String message = consoleRead.next();
					Game.sendMessage(null, recipient, "message", message);
					break;
				case 8: //View Message
					System.out.println("Retrieving list of messages");
					String messageList = Game.getMessageList();
					if(!messageList.equals("204") && messageList != null)
					{
						String messageID[] = messageList.split(",");
						System.out.println("Messages available:");
						for(String id:messageID)
						{
							System.out.println(id);
						}
						System.out.println("Please enter a message ID to view...");
						int mID = Integer.parseInt(consoleRead.next());
						String mailItem = Game.getMessage(mID);
						if(mailItem != null)
						{
							JSONObject mailJSON = new JSONObject(mailItem);
							System.out.println("Received: " + mailJSON.getString("Date") + "-" + mailJSON.getString("Time"));
							System.out.println("Message from: " + mailJSON.getString("Sender"));
							System.out.println("Message type: " + mailJSON.getString("Type"));
							System.out.println("Message contents: " + mailJSON.getString("Contents"));
						}
					}
					break;
				case 9:
					System.out.println("Retrieving list of messages");
					messageList = Game.getMessageList();
					if(!messageList.equals("204") && messageList != null)
					{
						String messageID[] = messageList.split(",");
						System.out.println("Messages available:");
						for(String id:messageID)
						{
							System.out.println(id);
						}
						System.out.println("Please enter a message ID to delete...");
						int mID = Integer.parseInt(consoleRead.next());
						Game.deleteMessage(mID);
						System.out.println("Message deleted");
					}
					break;
				case 10: 		//logout
					AsxGame.activePlayer = null;
					AsxGame.activePlayerLoaded = false;
					return;					
				case 0: 		//exit
					System.exit(0);
					break;
				default: System.out.println("Invalid Choice!");
			}
		}
	}

	protected static void adminMenu()
	{
		while (true)
		{		
			System.out.println("\nPlease select on of the following options: \n");
			System.out.println("	1. View admin account details");
			System.out.println("	2. Load a player to modify");
			System.out.println("	3. View loaded player account details");
			System.out.println("	4. Add stocks to loaded player");
			System.out.println("	5. Remove stocks from loaded player");
			System.out.println("	6. Set loaded players balance");
			System.out.println("	7. Save + unload loaded player");
			System.out.println("	9. Logout (NOTE: THIS DOES NOT SAVE YOUR PLAYER CURRENTLY!");
			System.out.println("	10. Set brokers fee on purchases");
			System.out.println("	11. Set brokers fee on sales");
			System.out.println("	12. Message all users");
			System.out.println("	0. Exit");
			
			input = consoleRead.next();
			inputInt = Integer.parseInt(input);
			switch (inputInt) 
			{
				case 1:		//Print Active Admin Details
					AsxGame.activeAdmin.printPlayer();
					break;
				case 2: 		//load player for admin to modify
					Admin.getUserList();
					ArrayList<String> playerList = Admin. returnPlayerList();
					for(String player:playerList)
					{
						System.out.println(player);
					}
					System.out.println("Enter a user email address");
					input = consoleRead.next();
					Admin.adminLoadPlayer(input);
					break;				
				case 3:		//View loaded player account details
					AsxGame.activePlayer.printPlayer();
					break;
				case 4:		//Add stocks to loaded player
					System.out.println("Enter a stock code, then qty to add to user");
					input = consoleRead.next();
					qty = Integer.parseInt(consoleRead.next());
					Admin.addStock(input, qty);
					break;
				case 5:		//Remove stocks from loaded player
					System.out.println("Enter a stock code, then qty to remove from user");
					input = consoleRead.next();
					qty = Integer.parseInt(consoleRead.next());
					Admin.removeStock(input, qty);
					break;
				case 6:		//Set loaded players balance
					System.out.println("Enter a number to change users balance to");
					amount = Float.parseFloat(consoleRead.next());
					Admin.changeBalance(amount);
					break;
				case 7:		//Save + unload loaded player
					System.out.println("Unloading player");
					Admin.adminUnloadPlayer();
					break;
				case 9: 		//logout
					AsxGame.activeAdminLoaded = false;
					AsxGame.activeAdmin = null;
					AsxGame.activePlayer = null;
					AsxGame.activePlayerLoaded = false;
					break;
				case 10: 		//set buy fees
					System.out.println("Enter flat fee then percentage fee");
					float flat = Float.parseFloat(consoleRead.next());
					float per = Float.parseFloat(consoleRead.next());
					Admin.setBuyFee(flat, per);
					break;
				case 11: 		//set sell fees
					System.out.println("Enter flat fee then percentage fee");
					flat = Float.parseFloat(consoleRead.next());
					per = Float.parseFloat(consoleRead.next());
					Admin.setSellFee(flat, per);
					break;
				case 12:
					System.out.println("Enter message to send to all users");
					String message = consoleRead.next();
					Admin.messageAllUsers("message", message);
					break;
				case 0: 		//exit
					System.exit(0);
					break;
				default: System.out.println("Invalid Choice!");
			}
		}
	}
}	
