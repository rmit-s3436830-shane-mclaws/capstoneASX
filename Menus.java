import java.util.*;
import java.io.*;

//temp control file while the project is console operated
public class Menus {	
	private static Scanner consoleRead = new Scanner(System.in);
	private static String input; 
	
	protected static void mainMenu(){
		while (true){
			System.out.println("PLayer List length" + asxGame.playerList.size());
			System.out.println("\nPlease select on of the following options: \n");
			if (asxGame.playerListLoaded == false){
				System.out.println("	1. Load Player Files to Player List");
			} else {
				System.out.println("	1. Reload Player Files to Player List");
			}
			if (asxGame.activePlayerLoaded == false){
				System.out.println("	2. Load player to active player");
			} else{
				System.out.println("	2. Load another player");
			}
			System.out.println("	3. Create new players");
			System.out.println("	0. Exit");
			
			input = consoleRead.next();
			switch (input) {
				case "1":		//"Load Player List"
					if (asxGame.loadPlayerList() == false);
						System.out.println("Failed to load Player List");
					asxGame.playerListLoaded = true;
					break;
				case "2": 		//"Load Player"
					asxGame.loadPlayer();
					asxGame.activePlayerLoaded = true;
					break;
				case "3":
					asxGame.createPlayers();	//
					break;
				case "4":
					asxGame.activePlayer.printPlayer();
					break;
				case "5":		//"Save active Player
					System.out.println(asxGame.activePlayer.savePlayerToFile());
					break;
				case "0": 		//exit
					System.exit(0);
					break;
				default: System.out.println("Invalid Choice!");
			}
		}
	}
}	
