package com.amazonaws.samples;

import java.util.*;

//temp control file while the project is console operated
public class Menus {	
	private static Scanner consoleRead = new Scanner(System.in);
	private static String input; 
	private static int inputInt; 
	
	protected static void menuLogin(){		
		System.out.println("\nPlease select on of the following options: \n");
		System.out.println("	1. Login with existing account");
		System.out.println("	2. Register new player");
		System.out.println("	3. Get temp asx data + print");
		System.out.println("	9. Load Offline Temp Player");
		System.out.println("	0. Exit");
		input = consoleRead.next();
		switch (input){
			case "1":
				menuLoginDialogue();
				return;
			case "2":
				menuRegisterDialogue();
				return;
			case "3":
				//Utilities.loadTempStockList();
				AsxPull.getAsxJson("ABP", "20170329");
				AsxGame.stockArray.get(0).printStock();
				AsxGame.stockArray.remove(0);
				return;
			case "9":
				Utilities.createTempOfflinePlayer();
				return;
			case "0": 		//exit
				System.exit(0);
				break;
		}
	}
	
	protected static void menuLoginDialogue(){
		String uEmail, password;
		System.out.println("Please enter username/email address: ");
		uEmail = consoleRead.next();
		System.out.println("Please enter password: ");
		password = consoleRead.next();
		Game.login(uEmail, password);
	}
	
	protected static void menuRegisterDialogue(){
		String name, surname, email, emailCheck, password, pwCheck;
		boolean emailCorrect = false;
		boolean passCorrect = false;
		boolean allCorrect = false;
		do {
			System.out.println("Please enter first name: ");
			name = consoleRead.next();
			System.out.println("Please enter surname: ");
			surname = consoleRead.next();
			do {
				System.out.println("Please enter email address: ");
				email = consoleRead.next();
				System.out.println("Please re-enter email address: ");
				emailCheck = consoleRead.next();
				if (email.equals(emailCheck)){
					emailCorrect = true;
				} else {
					System.out.println("Email adresses are different, please try again!");
				}			
			} while (emailCorrect == false);
			do {
				System.out.println("Please enter pasasword: ");
				password = consoleRead.next();
				System.out.println("Please re-enter password: ");
				pwCheck = consoleRead.next();
				if (password.equals(pwCheck)){
					passCorrect = true;
				} else {
					System.out.println("passwords are different, please try again!");
				}			
			} while (passCorrect == false);
			System.out.println("Your entered details are: ");
			System.out.println("Name: " + name + " " + surname);
			System.out.println("Email: " + email);
			System.out.println("Is this correct? Y/N");
			input  = consoleRead.next();
			System.out.println(input);
			if (input.equals("y") || input.equals("Y")){
				System.out.println("Attempting to register player");
				Game.registerPlayer(name, surname, email, password);
				allCorrect = true;
			}
		} while (allCorrect == false);
		
		
	}
	
	protected static void buyStockDialogue(){				//all dialogue for buying stocks
		if (AsxGame.stockArray.size() != 0){
			System.out.println("All available stocks: ");
			for (int i = 0; i < AsxGame.stockArray.size(); i++){
				AsxGame.stockArray.get(i).printStock();
			}
			System.out.println("Please select a stock to buy (give code, case sensitive)");
			input = consoleRead.next();
			for (int index = 0; index < AsxGame.stockArray.size(); index++){
				if (input.equals(AsxGame.stockArray.get(index).code)){
					System.out.println("Purchasing stock :" + AsxGame.stockArray.get(index).name);
					System.out.println("Enter number you wish to purchase");
					String input2 = consoleRead.next();
					inputInt = Integer.parseInt(input2);
					boolean success = Game.buyStocks(input, inputInt);
					if (success == true){
						System.out.println("Shares purchased");
					} else if (success == false){
						System.out.println("Shares NOT purchased");
					}
				}
			}
		}
	}
	
	protected static void sellStockDialogue(){					//all dialogue for selling stocks
		if (AsxGame.stockArray.size() != 0){
			System.out.println("Players stocks: ");
			AsxGame.activePlayer.printShares();
			System.out.println("Please select a stock to sell (give code, case sensitive)");
			input = consoleRead.next();
			for (int index = 0; index < AsxGame.activePlayer.shares.size(); index++){
				String[] shareSplit = AsxGame.activePlayer.shares.get(index).split(":");
				if (input.equals(shareSplit[0])){
					System.out.println("Selling stock :" + shareSplit[0]);
					System.out.println("Enter number you wish to sell");
					input = consoleRead.next();
					inputInt = Integer.parseInt(input);
					if (Game.sellStocks(shareSplit[0], inputInt))
						System.out.println("Shares Sold");
				}
			}
		}
	}
	
	protected static void mainMenu(){
		//Utilities.loadTempStockList();
		while (true){		
			System.out.println("\nPlease select on of the following options: \n");
			System.out.println("	1. Print Active Player Details");
			System.out.println("	2. List all availble stocks");
			System.out.println("	3. Buy stocks");
			System.out.println("	4. Sell stocks");
			System.out.println("	5. Save player to server");
			System.out.println("	6. Get Leaderboard");
	//		System.out.println("	L. Load stock list (only a select few stocks available currently");
			System.out.println("	9. Logout (NOTE: THIS DOES NOT SAVE YOUR PLAYER CURRENTLY!");
			System.out.println("	0. Exit");
			
			input = consoleRead.next();
			switch (input) {
				case "1":		//Print Active Player Details
					AsxGame.activePlayer.printPlayer();
					break;
				case "2": 		//List all available stocks
					System.out.println(AsxGame.stockArray.size());
					for (int i = 0; i < AsxGame.stockArray.size(); i++){
						AsxGame.stockArray.get(i).printStock();
					}
					break;
				case "3":		//Buy stocks
					buyStockDialogue();
					break;
				case "4":		//Sell Stocks
					sellStockDialogue();
					break;
				case "5":		//Save player to server
					//System.out.println(AsxGame.activePlayer.generateSaveString());
					Game.saveActivePlayer();
					break;
				case "6":
					Game.getValueLeaderboard();
					for (int i = 0; i < AsxGame.leaderboard.size(); i++){
						System.out.println(AsxGame.leaderboard.get(i));
					}
					break;
	//			case "L":
	//				Utilities.loadTempStockList();
	//				break;
				case "9": 		//logout
					AsxGame.activePlayer = null;
					AsxGame.activePlayerLoaded = false;
					return;
				case "0": 		//exit
					System.exit(0);
					break;
				default: System.out.println("Invalid Choice!");
			}
		}
	}
}	
