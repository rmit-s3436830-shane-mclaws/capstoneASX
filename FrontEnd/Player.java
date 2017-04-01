package com.amazonaws.samples;

import java.util.*;

public class Player {
	String name;
	String surname;
	String email;
	float balance;
	ArrayList<String> shares = new ArrayList<String>();	//formatted: "asxCode:Number"
	float score;
	boolean adminRights = false;
	
	public Player(String name, String surname, String email, float balance,
					String shareString, String score, String rights) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.balance= balance;
		if (shareString.length() != 0){
			String[] shareArray = shareString.split(",");
			for (int i = 0; i < shareArray.length; i++){
				String[] shareSplit = shareArray[i].split(":");
				addShares(shareSplit[0], Integer.parseInt(shareSplit[1]));
			}
		}
		this.score = Float.parseFloat(score);
		if (rights.equals("admin")){
			adminRights = true;
		}
	}

	void printPlayer(){
		System.out.println(this.name + " " + this.surname);
		System.out.println(this.email);
		System.out.println(this.balance);
		printShares();
		System.out.println("admin: " + adminRights);
		return;
	}
	
	Player getPlayer(){
		return this;
	}
	
	boolean addBalance(float amount){
		balance += amount;
		return true;
	}
	
	boolean removeBalance(float amount){
		balance -= amount;
		return true;
	}
	
	boolean setBalance(float amount){
		balance = amount;
		return true;
	}
	
	void printShares(){
		for (int i = 0; i < shares.size(); i++){
			System.out.println(shares.get(i));
		}
		return;
	}
	
	int getShareCount(String asxCode){
		String[] shareSplit = null;
		for (int i = 0; i < shares.size(); i++){
			shareSplit = shares.get(i).split(":");
			if (shareSplit[0].equals(asxCode)){
				return Integer.parseInt(shareSplit[1]);
			}
		}
		return 0;
	}
	
	boolean addShares(String asxCode, int number){
		boolean newShare = true;
		String[] shareSplit = null;
		for (int i = 0; i < shares.size(); i++){
			shareSplit = shares.get(i).split(":");
			if (shareSplit[0].equals(asxCode)){
				newShare = false;
				shares.remove(i);					//share is remove if existing and added back later
				break;
			}
		}
		if (newShare == true){						//if player doesn't already have shares of type
			String stringToAdd = asxCode + ":" + Integer.toString(number);
			shares.add(stringToAdd);
			return true;
		} else {
			if (shareSplit != null){				//if player already has shares of type
				int existingNumber = Integer.parseInt(shareSplit[1]);
				int newNumber = existingNumber + number;
				String stringToAdd = asxCode + ":" + Integer.toString(newNumber);
				shares.add(stringToAdd);
				return true;
			}
			return false;
		}
	}
	
	boolean removeShares(String asxCode, int number){
		String[] shareSplit;
		for (int i = 0; i < shares.size(); i++){
			shareSplit = shares.get(i).split(":");
			if (shareSplit[0].equals(asxCode)){
				shares.remove(i);	
				int oldNumber = Integer.parseInt(shareSplit[1]);	//remove shares to add back later
				if (number < oldNumber){							//not added back if all are sold
					int newNumber = oldNumber - number;
					String stringToAdd = asxCode + ":" + newNumber;
					shares.add(stringToAdd);
				}
				return true;
			}
		}
		return false;
	}
	
	String generateSaveString(){
		String sharesString = null;
		for (int i = 0; i < shares.size(); i++){
			if (i == 0){
				sharesString = shares.get(i);
			}
			if (i != 0){
				sharesString.concat(shares.get(i));
			}
			if (i != shares.size() -1){
				sharesString.concat(",");
			}
		}
		String rightsString;
		if(adminRights == true){
			rightsString = "admin";
		} else {
			rightsString = "trader";
		}
		String output = "{" + 
						"'Name':'" + name +
						"','Surname':'" + surname +
						"','Email':'" + email + 
						"','Balance':'" + Float.toString(balance) +
						"','Shares':'" + sharesString +
						"','Score':'" + Float.toString(score) + 
						"','Rights':'" + rightsString + "'}";
		System.out.println("Save String for player: " + output);
		return output;
	}
	
	/*public boolean savePlayerToFile(){
		try{
			FileOutputStream fos = new FileOutputStream(this.email +".sav");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this.email);
			oos.writeObject(this.name);
			oos.writeObject(this.balance);
			oos.close();
			fos.close();
			return true;
		} catch (IOException e){
			e.printStackTrace();			
			return false;
		}
	}*/
	
	/*public void loadPlayerFromFile(String filename) throws ClassNotFoundException{
		try{
			FileInputStream fis = new FileInputStream(filename + ".sav");
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.id = (int) ois.readObject();
			this.name = (String) ois.readObject();
			this.balance = (float) ois.readObject();
			ois.close();
			fis.close();
			//return true;
		} catch (IOException e) {
			//return false;
		}
	}*/
	
//	public boolean createPlayer(){
		
//	}
}
