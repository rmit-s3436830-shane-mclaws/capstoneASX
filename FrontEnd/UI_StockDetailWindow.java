package com.amazonaws.samples;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class UI_StockDetailWindow {
	
	static String transDate;
	static double price;
	
	static CategoryAxis xAxis = new CategoryAxis();
	static NumberAxis yAxis  = new NumberAxis();
	final static LineChart<String,Number> lineChart = new LineChart<String,Number>(xAxis, yAxis);
	static XYChart.Series<String, Number> series = new XYChart.Series<>();
	static boolean seriesAdded = false;
	
	static Label stocksOwnedLabel;
	
	static TextField buyField;
	static TextField sellField;
	
	static Stock requestedStock = null;
	
	public static void makeStockDetailWindow(String asxCode){
		
		/* GRAPH STARTS HERE */
		
		xAxis.setLabel("Date");
		yAxis.setLabel("Price");
		lineChart.setTitle(asxCode);
		series.setName(asxCode);
		
		int loopEndPoint = series.getData().size();
		for (int i = 0; i < loopEndPoint; i++){
			series.getData().remove(0);
		}
		
		LocalDateTime timePoint = LocalDateTime.now();
		LocalDate date = timePoint.toLocalDate();
		
		String[] dateSplit = date.toString().split("-");
		String endDateString = dateSplit[0]+dateSplit[1]+dateSplit[2];
		int endDateInt = Integer.parseInt(endDateString);
		date = date.minusDays(30);
		dateSplit = date.toString().split("-");
		String startDateString = dateSplit[0]+dateSplit[1]+dateSplit[2];
		int startDateInt = Integer.parseInt(startDateString);
		if (Game.getStockHistory(asxCode, startDateInt, endDateInt)){
			for (int i = 0; i < AsxGame.requestedStockHistory.size(); i++){
				System.out.println(AsxGame.requestedStockHistory.get(i).toString());
				transDate = AsxGame.requestedStockHistory.get(i).getString("Date");
				price = AsxGame.requestedStockHistory.get(i).getDouble("Ask Price");
				series.getData().add(new XYChart.Data<>(transDate, price));
			}
		}
		double upperBound = AsxGame.requestedStockHistory.get(0).getDouble("Ask Price");
		double lowerBound = AsxGame.requestedStockHistory.get(0).getDouble("Ask Price");
		double value;
		for (int i = 0; i < AsxGame.requestedStockHistory.size(); i++){
			value = AsxGame.requestedStockHistory.get(i).getDouble("Ask Price");
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
		yAxis.setTickUnit(1);
		if (!seriesAdded){
			lineChart.getData().add(series);
			seriesAdded = true;
		}	
		
		/* GRAPH ENDS HERE */
		
		BorderPane detailBorder = new BorderPane();
		BorderPane detailCenterBorder = new BorderPane();
		GridPane detailOptionGrid = new GridPane();
		
		GridPane stockDetailsGrid = new GridPane();
		stockDetailsGrid.setId("stockDetailGrid");
		stockDetailsGrid.setHgap(10);
		stockDetailsGrid.setVgap(10);
		stockDetailsGrid.setAlignment(Pos.CENTER);
		
		detailBorder.setId("overlayBackground");
		
		/* DAYS SHOWN BOX STARTS HERE */
		
		HBox detailDaysShownBox = new HBox();
		detailDaysShownBox.setAlignment(Pos.CENTER);
		Label daysShownLabel1 = new Label("Show graph for the last ");
		daysShownLabel1.setId("stockDetailText");
		TextField daysShownSelector = new TextField();
		daysShownSelector.setText("30");
		daysShownSelector.setMaxWidth(50);
		Label daysShownLabel2 = new Label(" days");
		daysShownLabel2.setId("stockDetailText");
		detailDaysShownBox.getChildren().addAll(daysShownLabel1, daysShownSelector, daysShownLabel2);
		
		/* DAYS SHOWN BOX ENDS HERE */
		
		/* STOCK DETAILS BOX STARTS HERE */
		
		for (int i = 0; i < AsxGame.stockArray.size(); i++){
			if (AsxGame.stockArray.get(i).code.equals(AsxGame.requestedStockCode)){
				requestedStock = AsxGame.stockArray.get(i);
			}
		}
		
		Label stockCodeName = new Label(requestedStock.code + " - " + requestedStock.name);
		stockCodeName.setId("stockDetailText");
		HBox stockCodeNameBox = new HBox(stockCodeName);
		stockCodeNameBox.setAlignment(Pos.CENTER);
		stockDetailsGrid.add(stockCodeNameBox, 0, 0, 8, 1);
				
		Label priceLabel = new Label("Current Price:");
		priceLabel.setId("stockDetailText");
		HBox priceLabelBox = new HBox(priceLabel);
		Label askPrice = new Label("$ " + Float.toString(requestedStock.askPrice));
		askPrice.setId("stockDetailText");
		HBox askPriceBox = new HBox(askPrice);
		stockDetailsGrid.add(priceLabelBox, 0, 1);
		stockDetailsGrid.add(askPriceBox, 1, 1);
		
		Label changeLabel = new Label("% Change:");
		changeLabel.setId("stockDetailText");
		HBox changeLabelBox = new HBox(changeLabel);
		Label changePercent = new Label(requestedStock.changePercent);
		changePercent.setId("stockDetailText");
		HBox changePercentBox = new HBox(changePercent);
		stockDetailsGrid.add(changeLabelBox, 0, 2);
		stockDetailsGrid.add(changePercentBox, 1, 2);
		
		Label yearHighLabel = new Label("Past Year High:");
		yearHighLabel.setId("stockDetailText");
		HBox yearHighLabelBox = new HBox(yearHighLabel);
		Label pastYearHigh = new Label("$ " + requestedStock.pastYearHigh);
		pastYearHigh.setId("stockDetailText");
		HBox pastYearHighBox = new HBox(pastYearHigh);
		stockDetailsGrid.add(yearHighLabelBox, 6, 1);
		stockDetailsGrid.add(pastYearHighBox, 7, 1);
		
		Label yearLowLabel = new Label("Past Year Low:");
		yearLowLabel.setId("stockDetailText");
		HBox yearLowLabelBox = new HBox(yearLowLabel);
		Label pastYearLow = new Label("$ " + requestedStock.pastYearLow);
		pastYearLow.setId("stockDetailText");
		HBox pastYearLowBox = new HBox(pastYearLow);
		stockDetailsGrid.add(yearLowLabelBox, 6, 2);
		stockDetailsGrid.add(pastYearLowBox, 7, 2);
		
		stockDetailsGrid.setPadding(new Insets(0, 0, 25, 0));
		
		/* STOCK DETAILS BOX ENDS HERE */
		
		/* RIGHT OPTIONS BAR STARTS HERE */
		
		stocksOwnedLabel = new Label();
		stocksOwnedLabel.setText("You own: " + AsxGame.activePlayer
													.getShareCount(requestedStock.code));
		stocksOwnedLabel.setId("stockDetailText");
		detailOptionGrid.add(stocksOwnedLabel, 0, 0);
		
		buyField = new TextField();
		buyField.setPromptText("Amount to buy");
		detailOptionGrid.add(buyField, 0, 1);
		
		Button buyButton = new Button("Buy");
		buyButton.setOnAction(e-> buyButtonClicked(e));
		detailOptionGrid.add(buyButton, 0, 2);
		
		sellField = new TextField();
		sellField.setPromptText("Amount to sell");
		if (AsxGame.activePlayer.getShareCount(requestedStock.code) == 0){
			sellField.setDisable(true);
		}
		detailOptionGrid.add(sellField, 0, 3);
		
		Button sellButton = new Button("Sell");
		sellButton.setOnAction(e-> sellButtonClicked(e));
		detailOptionGrid.add(sellButton, 0, 4);
		
		Button backButton = new Button("Back");
		backButton.setMinSize(50, 50);
		backButton.setOnAction(e-> backButtonClicked(e));
		detailOptionGrid.add(backButton, 0, 5);
		
		/* RIGHT OPTIONS BAR ENDS HERE */
				
		/* SETTING AREAS */
		
		detailCenterBorder.setTop(lineChart);
	//	detailCenterBorder.setCenter(detailDaysShownBox);
		detailCenterBorder.setBottom(stockDetailsGrid);
		
		detailBorder.setCenter(detailCenterBorder);
		detailBorder.setRight(detailOptionGrid);
		
		UI_MainScene.homeScreenStack.getChildren().add(detailBorder);
	}
	
	private static void backButtonClicked(ActionEvent e){
		int stackSize = UI_MainScene.homeScreenStack.getChildren().size();
		UI_MainScene.homeScreenStack.getChildren().remove(stackSize - 1);
	}
	
	private static void buyButtonClicked(ActionEvent e){
		String buyFieldString = buyField.getText();
		int buyFieldInt = 0;
		try {
			buyFieldInt = Integer.parseInt(buyFieldString);
		} catch (NumberFormatException excep) {
			
		}
		
		if (buyFieldInt > 0){
			if (Game.buyStocks(requestedStock.code, buyFieldInt)){
				int newShareCount = AsxGame.activePlayer.getShareCount(requestedStock.code);
				stocksOwnedLabel.setText("You own: " + newShareCount);
				if (newShareCount > 0){
					sellField.setDisable(false);
				}
				AsxGame.UI_MainScene.updateTopBar();
			}
			
			
		}
	}
	
	private static void sellButtonClicked(ActionEvent e){
		String sellFieldString = sellField.getText();
		int sellFieldInt = 0;
		try {
			sellFieldInt = Integer.parseInt(sellFieldString);
		} catch (NumberFormatException excep) {
			
		}
		if (sellFieldInt > 0){
			Game.sellStocks(requestedStock.code, sellFieldInt);
			AsxGame.UI_MainScene.updateTopBar();
			stocksOwnedLabel.setText("You own: " + AsxGame.activePlayer
					.getShareCount(requestedStock.code));
		}
	}
}
