package com.amazonaws.samples;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javafx.event.ActionEvent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class UI_StockDetailWindow {
	
	static String transDate;
	static double price;
	
	static CategoryAxis xAxis = new CategoryAxis();
	static NumberAxis yAxis  = new NumberAxis();
	final static LineChart<String,Number> lineChart = new LineChart<String,Number>(xAxis, yAxis);
	static XYChart.Series<String, Number> series = new XYChart.Series<>();
	static boolean seriesAdded = false;
	
	
	public static void makeStockDetailWindow(String asxCode){
		
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
		
		BorderPane detailBorder = new BorderPane();
		GridPane detailOptionGrid = new GridPane();
		detailBorder.setId("overlayBackground");
		
		Button backButton = new Button("Back");
		backButton.setMinSize(50, 50);
		backButton.setOnAction(e-> backButtonClicked(e));
		detailOptionGrid.add(backButton, 0, 0);
		
		detailBorder.setCenter(lineChart);
		detailBorder.setRight(detailOptionGrid);
		
		UI_MainScene.homeScreenStack.getChildren().add(detailBorder);
		UI_MainScene.stockDetailWindowVisible = true;
	}
	
	public static void unloadChart(){
		
	}
	
	private static void backButtonClicked(ActionEvent e){
		int stackSize = UI_MainScene.homeScreenStack.getChildren().size();
		UI_MainScene.homeScreenStack.getChildren().remove(stackSize - 1);
		UI_MainScene.stockDetailWindowVisible = false;
	}
}
