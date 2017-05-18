package com.amazonaws.samples;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

public class UI_Leaderboard {

	private final static TableView<LeaderboardRow> table = new TableView<>();
	private static ObservableList<LeaderboardRow> tableList = FXCollections.observableArrayList();
	
	static String name;
	static String score;
	
	
	@SuppressWarnings("unchecked")
	public static void makeLeaderBoardWindow(){
		if (Game.getValueLeaderboard() == false){
			return;
		}
		
		while (tableList.size() != 0){
			tableList.remove(0);
		}
		
		while (table.getColumns().size() != 0){
			table.getColumns().remove(0);
		}
		
		TableColumn<LeaderboardRow, String> posCol = 
				new TableColumn<LeaderboardRow, String>("Position");
		posCol.setCellValueFactory(
				new PropertyValueFactory<>("pos"));
		
		TableColumn<LeaderboardRow, String> nameCol = 
				new TableColumn<LeaderboardRow, String>("Name");
		nameCol.setCellValueFactory(
				new PropertyValueFactory<>("name"));
		nameCol.setSortable(false);
		
		TableColumn<LeaderboardRow, String> scoreCol = 
				new TableColumn<LeaderboardRow, String>("Score");
		scoreCol.setCellValueFactory(
				new PropertyValueFactory<>("score"));
		scoreCol.setSortable(false);
		
		table.getColumns().addAll(posCol, nameCol, scoreCol);
		
		for (int i = 0; i < AsxGame.leaderboard.size(); i++){
			name = AsxGame.leaderboard.get(i).getString("Name") + " " +  
					AsxGame.leaderboard.get(i).getString("Surname");
			score = AsxGame.leaderboard.get(i).getString("Score");
			tableList.add(new LeaderboardRow(i + 1, name, score));
		}
		
		table.setItems(tableList);
		
		BorderPane leaderBorder = new BorderPane();
		leaderBorder.setCenter(table);
		
		Button backButton = new Button("Back");
		backButton.setMinSize(50, 50);
		backButton.setOnAction(e-> backButtonClicked(e));
		
		leaderBorder.setRight(backButton);
		
		UI_MainScene.homeScreenStack.getChildren().add(leaderBorder);
	}
	
	private static void backButtonClicked(ActionEvent e){
		int stackSize = UI_MainScene.homeScreenStack.getChildren().size();
		UI_MainScene.homeScreenStack.getChildren().remove(stackSize -1);
		UI_MainScene.leaderWindowVis = false;
		UI_MainScene.menuLeaderRect.setStyle("-fx-fill:black;");
	}
	
	
	public static class LeaderboardRow{
		private final SimpleIntegerProperty pos;
		private final SimpleStringProperty name;
		private final SimpleStringProperty score;
		
		private LeaderboardRow(int pos, String name, String score){
			this.pos = new SimpleIntegerProperty(pos);
			this.name = new SimpleStringProperty(name);
			this.score = new SimpleStringProperty(score);
		}
		
		public int getPos(){
			return pos.get();
		}
		
		public void setPos(int in){
			pos.set(in);
		}
		
		public String getName(){
			return name.get();
		}
		
		public void setName(String in){
			name.set(in);
		}
		
		public String getScore(){
			return score.get();
		}
		
		public void setScore(String in){
			score.set(in);
		}
	}
	
	
}
