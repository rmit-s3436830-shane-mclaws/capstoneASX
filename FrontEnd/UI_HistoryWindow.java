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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
	
	static CategoryAxis xAxis = new CategoryAxis();
	static NumberAxis yAxis  = new NumberAxis();
	final static LineChart<String,Number> lineChart = new LineChart<String,Number>(xAxis, yAxis);
	static XYChart.Series<String, Number> series = new XYChart.Series<>();
	static boolean seriesAdded = false;
	
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
		
		HBox tableBox = new HBox();
		tableBox.setPadding(new Insets(10,10,10,10));
		
		/* GRAPH STARTS HERE */
		
		xAxis.setLabel("Date");
		yAxis.setLabel("Player Value");
		lineChart.setTitle("Player Value History");
		series.setName("Player Value");
		
		int loopEndPoint = series.getData().size();
		for (int i = 0; i < loopEndPoint; i++){
			series.getData().remove(0);
		}
		
		for (int i = 0; i < AsxGame.activePlayer.valueHistory.size(); i++){
			String date = AsxGame.activePlayer.valueHistory.get(i).getString("Date");
			double price = AsxGame.activePlayer.valueHistory.get(i).getDouble("Value");
			series.getData().add(new XYChart.Data<>(date, price));
		}
		
		double upperBound = AsxGame.activePlayer.valueHistory.get(0).getDouble("Value");
		double lowerBound = AsxGame.activePlayer.valueHistory.get(0).getDouble("Value");
		double value;
		for (int i = 0; i < AsxGame.activePlayer.valueHistory.size(); i++){
			value = AsxGame.activePlayer.valueHistory.get(i).getDouble("Value");
			if (value > upperBound){
				upperBound = value;
			}
			if (value < lowerBound){
				lowerBound = value;
			}
		}
		double graphUpperBound = upperBound + (upperBound - lowerBound) * 0.2;
		double graphLowerBound = lowerBound - (upperBound - lowerBound) * 0.2;
		System.out.println("Upper: " + upperBound + ", Lower: " + lowerBound);
		
		yAxis.setAutoRanging(false);
		yAxis.setUpperBound(graphUpperBound);
		yAxis.setLowerBound(graphLowerBound);
		yAxis.setTickUnit(10000);
		if (!seriesAdded){
			lineChart.getData().add(series);
			seriesAdded = true;
		}	
		
		/* GRAPH ENDS HERE */
		
		tableBox.getChildren().addAll(lineChart, table);
		
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
