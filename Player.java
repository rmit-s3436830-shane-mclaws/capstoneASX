import java.io.*;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Player {
	int id;
	String name;
	float cash;
	
	public Player(int id, String name, float cash) {
		this.id = id;
		this.name = name;
		this.cash= cash;
	}

	void printPlayer(){
		System.out.println(this.id);
		System.out.println(this.name);
		System.out.println(this.cash);
		return;
	}
	
	Player getPlayer(){
		return this;
	}
	
	boolean addCash(int amount){
		cash += amount;
		return true;
	}
	
	boolean removeCash(int amount){
		cash -= amount;
		return true;
	}
	
	public boolean savePlayerToFile(){
		try{
			FileOutputStream fos = new FileOutputStream(this.id +".sav");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this.id);
			oos.writeObject(this.name);
			oos.writeObject(this.cash);
			oos.close();
			fos.close();
			return true;
		} catch (IOException e){
			e.printStackTrace();			
			return false;
		}
	}
	
	public void loadPlayerFromFile(String filename) throws ClassNotFoundException{
		try{
			FileInputStream fis = new FileInputStream(filename + ".sav");
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.id = (int) ois.readObject();
			this.name = (String) ois.readObject();
			this.cash = (float) ois.readObject();
			ois.close();
			fis.close();
			//return true;
		} catch (IOException e) {
			//return false;
		}
	}
	
//	public boolean createPlayer(){
		
//	}
}
