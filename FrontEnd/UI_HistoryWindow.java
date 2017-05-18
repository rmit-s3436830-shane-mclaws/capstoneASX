package com.amazonaws.samples;

import org.json.JSONObject;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class UI_HistoryWindow {
	
	
	private final static TableView<HistoryTableRow> table = new TableView<>();
	private static ObservableList<HistoryTableRow> tableList = FXCollections.observableArrayList();
	
	@SuppressWarnings("unchecked")
	public static void initHistoryTable(){
		TableColumn<HistoryTableRow, String> dateCol = 
				new TableColumn<HistoryTableRow, String>("Date");
		dateCol.setMinWidth(100);
		dateCol.setSortable(false);
		dateCol.setCellValueFactory(
				new PropertyValueFactory<>("date"));
		
		TableColumn<HistoryTableRow, String> timeCol = 
				new TableColumn<HistoryTableRow, String>("Time");
		timeCol.setMinWidth(100);
		timeCol.setSortable(false);
		timeCol.setCellValueFactory(
				new PropertyValueFactory<>("time"));
		
		TableColumn<HistoryTableRow, String> codeCol = 
				new TableColumn<HistoryTableRow, String>("Code");
		codeCol.setMinWidth(100);
		codeCol.setSortable(false);
		codeCol.setCellValueFactory(
				new PropertyValueFactory<>("code"));
		
		TableColumn<HistoryTableRow, String> typeCol = 
				new TableColumn<HistoryTableRow, String>("Type");
		typeCol.setMinWidth(100);
		typeCol.setSortable(false);
		typeCol.setCellValueFactory(
				new PropertyValueFactory<>("transType"));
		
		TableColumn<HistoryTableRow, String> numberCol = 
				new TableColumn<HistoryTableRow, String>("Number");
		numberCol.setMinWidth(100);
		numberCol.setSortable(false);
		numberCol.setCellValueFactory(
				new PropertyValueFactory<>("number"));
		
		TableColumn<HistoryTableRow, String> priceCol = 
				new TableColumn<HistoryTableRow, String>("Price");
		priceCol.setMinWidth(100);
		priceCol.setSortable(false);
		priceCol.setCellValueFactory(
				new PropertyValueFactory<>("price"));
		
		
		TableColumn<HistoryTableRow, String> valueCol = 
				new TableColumn<HistoryTableRow, String>("Value");
		valueCol.setMinWidth(100);
		valueCol.setSortable(false);
		valueCol.setCellValueFactory(
				new PropertyValueFactory<>("value"));
		
		table.getColumns().addAll(dateCol, timeCol, codeCol, typeCol, 
				numberCol, priceCol, valueCol);
		table.setItems(tableList);
	}
	
	public static void makeHistoryWindow(){
		BorderPane histBorder = new BorderPane();
		histBorder.setId("overlayBackground");
		GridPane histOptionsGrid = new GridPane();
		histOptionsGrid.setAlignment(Pos.CENTER);
		
		HBox tableBox = new HBox(table);
		tableBox.setPadding(new Insets(10,10,10,10));
		histBorder.setCenter(tableBox);
		
		Button backButton = new Button("Back");
		backButton.setMinSize(50, 50);
		backButton.setOnAction(e-> backButtonClicked(e));
		
		histOptionsGrid.add(backButton, 0, 0);
		histBorder.setRight(histOptionsGrid);
		
		updateHistoryTable();
		
		UI_MainScene.homeScreenStack.getChildren().add(histBorder);
		
	}
	
	private static void backButtonClicked(ActionEvent e){
		int stackSize = UI_MainScene.homeScreenStack.getChildren().size();
		UI_MainScene.homeScreenStack.getChildren().remove(stackSize -1);
		UI_MainScene.menuHistRect.setStyle("-fx-fill:black;");
	}
	
	public static void updateHistoryTable(){
		System.out.println("updateHistoryTableCalled");
		while (tableList.size() != 0){
			tableList.remove(0);
		}
		for (int i = AsxGame.activePlayer.transHistory.size() -1; i >= 0; i--){
			System.out.println(AsxGame.activePlayer.transHistory.get(i).toString());
			tableList.add(new HistoryTableRow(AsxGame.activePlayer.transHistory.get(i)));
		}
	}
	
	public static class HistoryTableRow{
		private final SimpleStringProperty date;
		private final SimpleStringProperty time;
		private final SimpleStringProperty transType;
		private final SimpleStringProperty code;
		private final SimpleIntegerProperty number;
		private final SimpleFloatProperty price;
		private final SimpleFloatProperty value;
		
		private HistoryTableRow(JSONObject json){
			date = new SimpleStringProperty(json.getString("Date"));
			time = new SimpleStringProperty(json.getString("Time"));
			transType = new SimpleStringProperty(json.getString("TransType"));
			code = new SimpleStringProperty(json.getString("ASXCode"));
			number = new SimpleIntegerProperty(json.getInt("Number"));
			float priceFloat = (float)json.getDouble("Price");
			price = new SimpleFloatProperty(priceFloat);
			float valueFloat = json.getInt("Number") * (float)json.getDouble("Price");
			value  = new SimpleFloatProperty(valueFloat);
		}
		
		public String getDate(){
			return date.get();
		}
		
		public void setDate(String in){
			date.set(in);
		}
		
		public String getTime(){
			return time.get();
		}
		
		public void setTime(String in){
			time.set(in);
		}
		
		public String getTransType(){
			return transType.get();
		}
		
		public void setTransType(String in){
			transType.set(in);
		}
		
		public String getCode(){
			return code.get();
		}
		
		public void setCode(String in){
			code.set(in);
		}
		
		public int getNumber(){
			return number.get();
		}
		
		public void setNumber(int in){
			number.set(in);
		}
		
		public float getPrice(){
			return price.get();
		}
		
		public void setPrice(float in){
			price.set(in);
		}
		
		public float getValue(){
			return value.get();
		}
		
		public void setValue(float in){
			value.set(in);
		}
	}
}
