package com.amazonaws.samples;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import org.json.JSONObject;

public class Player {
	String name;
	String surname;
	String email;
	float balance;
	ArrayList<String> shares = new ArrayList<String>();	//formatted: "asxCode:Number"
	ArrayList<JSONObject> transHistory = new ArrayList<JSONObject>(); 	//formatted as JSON; keys:date,time,buy/sell,stock code,number,price
	float score;												
	boolean adminRights = false;
	float shareVal;
	float totalValue;
	
	public Player(String name, String surname, String email, float balance,
					String shareString, String score, String rights, String transHist) {
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
		String[] transSplit = transHist.split("\n");
		if (!transHist.equals("")){
			for (int i = 0; i < transSplit.length; i++){
				JSONObject json = new JSONObject(transSplit[i]);
				transHistory.add(json);
			}
		}
		shareVal = 0;
	//	calcValue();
	}

	void printPlayer(){
		System.out.println(this.name + " " + this.surname);
		System.out.println(this.email);
		System.out.println(this.balance);
		System.out.println("Worth: " + score);
		printShares();
		System.out.println("admin: " + adminRights);
		for (int i = 0; i < transHistory.size(); i++){
			System.out.println(transHistory.get(i).toString());
		}
		generateDataSaveString();
		return;
	}
		
	float calcValue(){
		shareVal = 0;						//reset when starting calculation
		
		for (int i = 0; i < shares.size(); i++){
			String[] shareSplit = shares.get(i).split(":");
			for (int j = 0; j < AsxGame.stockArray.size(); j++){
				if (AsxGame.stockArray.get(j).code.equals(shareSplit[0])){
					shareVal += AsxGame.stockArray.get(j).askPrice
									* Integer.parseInt(shareSplit[1]);
				}
			}
		}
		score = shareVal + balance - 1000000;
		totalValue = shareVal + balance;
		return totalValue;
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
	
	boolean updateTransHist(String dateIn, String timeIn, String asxCodeIn, String transTypeIn, int numberIn, float priceIn){
		JSONObject json = new JSONObject();
		if (Integer.parseInt(dateIn) == -1 || Integer.parseInt(timeIn) == -1){	
			LocalDateTime timePoint = LocalDateTime.now();
			LocalDate date = timePoint.toLocalDate();
			LocalTime time = timePoint.toLocalTime();
			String day = Integer.toString(date.getDayOfMonth());
			if (day.length() == 1){
				day = "0"+day;
			}
			String month = Integer.toString(date.getMonthValue());
			if (month.length() == 1){
				month = "0"+month;
			}
			String year = Integer.toString(date.getYear());
			String dateString = year + month + day;
			String timeString = time.toString();
			json.put("Date", dateString);
			json.put("Time", timeString);
		} else {
			json.put("Date", dateIn);
			json.put("Time", timeIn);
		}
		json.put("TransType", transTypeIn);
		json.put("ASXCode", asxCodeIn);
		json.put("Number", numberIn);
		json.put("Price", priceIn);
		transHistory.add(json);
		Game.saveActivePlayer(json);
		return true;
	}
	
	String generateDataSaveString(){
		String sharesString = "";
		for (int i = 0; i < shares.size(); i++){
			if (i == 0){
				sharesString = shares.get(i);
			}
			if (i != 0){
				sharesString += shares.get(i);
			}
			if (i != shares.size() -1){
				sharesString += ",";
			}
		}
		
		String rightsString;
		if(adminRights == true){
			rightsString = "admin";
		} else {
			rightsString = "trader";
		}
		JSONObject json = new JSONObject();
		json.put("Name", name);
		json.put("Surname", surname);
		json.put("Email", email);
		json.put("Balance", Float.toString(balance));
		json.put("Shares", sharesString);
		json.put("Score", Float.toString(score));
		json.put("Rights", rightsString);
		String output = json.toString();
		System.out.println("SaveString: " + output);
		return output;
	}
	
	public String getBalanceToString(){
		return Float.toString(balance);
	}
}
