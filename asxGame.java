import java.util.*;
import java.io.*;

public class asxGame {

	public static ArrayList<Player> playerList = new ArrayList<Player>();
	public static Player activePlayer;
	public static boolean playerListLoaded = false;
	public static boolean activePlayerLoaded = false;
	private static Scanner consoleRead = new Scanner(System.in);
	private static String input; 
	
	public static void main(String[] args){
		System.out.println(
				"Welcome to Programming Project 1 - Stock Market game\n\n"
				
				+ "A Project by:\n"
				+ "		Shane McLaws (s3436830)\n"
				+ "		Callum Pullyblank (s3378543)\n"
				+ "		Zac Williams (s3431670)\n"
				+ "		Sonia Varghese (s3484881)\n\n"
				+ "Press Enter to continue..."
		);
		try{
			System.in.read();
		} catch(IOException e) {
			e.printStackTrace();
		}
		Menus.mainMenu();
		
	}
	
/*	private static void mainMenu(){
		
		while(true){	
			System.out.println("PLayer List length" + playerList.size());
			System.out.println("\nPlease select on of the following options: \n");
			if (playerListLoaded == false){
				System.out.println("	1. Load Player Files to Player List");
			} else {
				System.out.println("	1. Reload Player Files to Player List");
			}
			if (activePlayerLoaded == false){
				System.out.println("	2. Load player to active player");
			} else{
				System.out.println("	2. Load another player");
			}
			System.out.println("	3. Create new players");
			System.out.println("	0. Exit");
			
			input = consoleRead.next();
			switch (input) {
				case "1":		//"Load Player List"
					if (loadPlayerList() == false);
						System.out.println("Failed to load Player List");
					playerListLoaded = true;
					break;
				case "2": 		//"Load Player"
					loadPlayer();
					activePlayerLoaded = true;
					break;
				case "3":
					createPlayers();
					break;
			//	case "4":
				//	Menus.tempReadList();
				//	break;
				case "5":		//"Save active Player
					System.out.println(activePlayer.savePlayerToFile());
					break;
				case "0": 		//exit
					System.exit(0);
					break;
				default: System.out.println("Invalid Choice!");
			}
		}
	}*/
	
	public static boolean loadPlayer(){
		//loads a player from the list to activeplayer
		if (playerList.size() != 0 ) {
			System.out.println("Choose player to view:\n");
			input = consoleRead.next();
			int choice = Integer.parseInt(input);
			if (choice >= 1 && choice <= 6){
				activePlayer = playerList.get(choice - 1).getPlayer();
			}
		}
		return true;
	}
	
	public static boolean createPlayers(){				//deprecated method to load test players
		playerList.add(new Player (1, "Bob", 14000));	//now (hopefully) reading from file
		playerList.add(new Player (2, "John", 14000));
		playerList.add(new Player (3, "Joe", 14000));
		playerList.add(new Player (4, "Jane", 14000));
		playerList.add(new Player (5, "Terry", 14000));
		playerList.add(new Player (6, "Bruce", 14000));
		for (int i = 0; i < 6; i++){
			activePlayer = playerList.get(i).getPlayer();
			System.out.println(activePlayer.savePlayerToFile());
		}
		return true;
	}
	
	/*private static boolean createNewPlayer(){
		//creates a new player
		int assignedID = 0;
		String name, choice;
		Player newPlayer;
		if (playerList.size() == 0){	//opens the player list if it isnt already
			loadPlayerList();
		}
		for (int i = 0; i < playerList.size(); i++){ //finds ID of highest existing player
			if (playerList.get(i).id > assignedID){
				assignedID = playerList.get(i).id;
			}
		}
		assignedID++;		//add 1 for new players ID
		while(true){
			System.out.println("Please enter your name: ");
			name = consoleRead.next();
			System.out.println("So your name is: " + name + "\nIs this correct? Y/N");
			choice = consoleRead.next();
			System.out.println("Choice:"+choice);
		//	if (choice == "y"){			//THIS SHOULD BE WORKING BUT ISNT, COME BACK HERE!
				newPlayer = new Player(assignedID, name, 1000000);
				playerList.add(newPlayer);
				newPlayer.savePlayerToFile();
				activePlayer = newPlayer;
				activePlayer.printPlayer();
				return true;
		//	} else {
		//		System.out.println("Please try again!"); //Need to fix how this handles incorrect inputs
		//	}
		}
	}*/
	
	public static boolean loadPlayerList(){
	//loads a list of all players currently with active accounts on device
		
		for (int i = 0; i < 6; i++){
			Player temp = new Player (0,"0",0);
			try{
				temp.loadPlayerFromFile("1"/*Integer.toString(i+1)*/);
			} catch (ClassNotFoundException e){
				return false;
			}
			playerList.add(temp);
		}
		return true;
	}
}
