package com.amazonaws.samples;

import java.util.ArrayList;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import org.json.*;

public class UI_Admin {
	
	static ArrayList<String> playerList = new ArrayList<String>();
	private final static TableView<PlayerListRow> table = new TableView<>();
	private static ObservableList<PlayerListRow> tableList = FXCollections.observableArrayList();
	
	@SuppressWarnings("unchecked")
	public static void makePlayersTable(){
		
		while (tableList.size() != 0){
			tableList.remove(0);
		}
		while (table.getColumns().size() != 0){
			table.getColumns().remove(0);
		}
		
		Admin.getUserList();
		playerList = Admin.returnPlayerList();
		
		TableColumn<PlayerListRow, String> idCol = 
				new TableColumn<PlayerListRow, String>("ID");
		idCol.setCellValueFactory(
				new PropertyValueFactory<>("id"));
		
		TableColumn<PlayerListRow, String> emailCol = 
				new TableColumn<PlayerListRow, String>("Email");
		emailCol.setCellValueFactory(
				new PropertyValueFactory<>("email"));
		
		table.getColumns().addAll(idCol, emailCol);
		
		for (int i = 0; i < playerList.size(); i++){
			JSONObject json = new JSONObject(playerList.get(i));
			String email = json.getString("Email");
			tableList.add(new PlayerListRow(i, email));
		}
		
		table.setRowFactory( tv -> {
			TableRow<PlayerListRow> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())){
					PlayerListRow rowData = row.getItem();
					System.out.println("Double Clicked " + rowData.getEmail());
					Admin.adminLoadPlayer(rowData.getEmail());
					if (AsxGame.activePlayerLoaded){
						UI_MainScene.homeScreenStack.getChildren().remove(0);
						UI_Portfolio.makePortfolioView();
					}
				}
			});
			return row;
		});
		
		FilteredList<PlayerListRow> filteredList = new FilteredList<>(tableList, p -> true);
		
		VBox adminPlayerListBox = new VBox();
		
		TextField searchField = new TextField();
		searchField.setPromptText("Search for user");
		
		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredList.setPredicate(PlayerListRow -> {
				// If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (PlayerListRow.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches email.
                } 
                return false; // Does not match.
			});
		});
		
		SortedList<PlayerListRow> sortedList = new SortedList<>(filteredList);
		
		sortedList.comparatorProperty().bind(table.comparatorProperty());
		
		table.setItems(sortedList);
		
		adminPlayerListBox.getChildren().addAll(searchField, table);
		
		UI_MainScene.homeScreenStack.getChildren().removeAll(UI_MainScene.homeScreenStack);
		UI_MainScene.homeScreenStack.getChildren().add(adminPlayerListBox);
		
		
	}
	
	public static class PlayerListRow{
		
		private final SimpleIntegerProperty id;
		private final SimpleStringProperty email;
		
		private PlayerListRow(int id, String email){
			this.id = new SimpleIntegerProperty(id);
			this.email = new SimpleStringProperty(email);
		}
		
		public int getId(){
			return id.get();
		}
		
		public void setId(int in){
			id.set(in);
		}
		
		public String getEmail(){
			return email.get();
		}
		
		public void setEmail(String in){
			email.set(in);
		}
	}
}
