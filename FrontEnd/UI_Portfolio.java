package com.amazonaws.samples;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UI_Portfolio {
	VBox tableBox;
	static BorderPane portfolioBorder;
	
	/* Portfolio Variables */
	private final static TableView<PortfolioTableRow> table = new TableView<>();
	private static ObservableList<PortfolioTableRow> tableList = FXCollections.observableArrayList();
	
	@SuppressWarnings("unchecked")
	public static void initPortfolioTable(){
		
		TableColumn<PortfolioTableRow, String> asxCodeColumn = new TableColumn<PortfolioTableRow, String>("Stock Code");
		asxCodeColumn.setMinWidth(100);
		asxCodeColumn.setCellValueFactory(
				new PropertyValueFactory<>("tableStockCode"));
		
		TableColumn<PortfolioTableRow, String> stockNameColumn = new TableColumn<PortfolioTableRow, String>("Name");
		stockNameColumn.setMinWidth(300);
		stockNameColumn.setCellValueFactory(
				new PropertyValueFactory<>("tableStockName"));
		
		TableColumn<PortfolioTableRow, String> priceColumn = new TableColumn<PortfolioTableRow, String>("Current Price");
		priceColumn.setMinWidth(100);
		priceColumn.setCellValueFactory(
				new PropertyValueFactory<>("tablePrice"));
		
		TableColumn<PortfolioTableRow, String> numOwnedColumn = new TableColumn<PortfolioTableRow, String>("Number Owned");
		numOwnedColumn.setMinWidth(100);
		numOwnedColumn.setCellValueFactory(
				new PropertyValueFactory<>("tableNumber"));
		
		table.getColumns().addAll(asxCodeColumn, stockNameColumn, priceColumn, numOwnedColumn);
	}
	
	public static void makePortfolioTable(){
		table.setEditable(true);
		VBox tableBox = new VBox(table);
		tableBox.setMaxWidth(642);
		portfolioBorder = new BorderPane();
		portfolioBorder.setCenter(tableBox);
		portfolioBorder.setId("portfolioBorder");
		
				
		tableBox.setAlignment(Pos.CENTER);
		tableBox.setPadding(new Insets(0,20,50,20));
		
		UI_MainScene.homeScreenStack.getChildren().add(portfolioBorder);
		
		HBox headerBox = new HBox();
		Label portfolioHeader = new Label("YOUR CURRENT PORTFOLIO");
		portfolioHeader.setId("portfolioHeader");
		headerBox.getChildren().add(portfolioHeader);
		headerBox.setAlignment(Pos.CENTER);
		headerBox.setPadding(new Insets(20,160,20,0));
		portfolioBorder.setTop(headerBox);
		
		table.setRowFactory( tv -> {
			TableRow<PortfolioTableRow> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())){
					PortfolioTableRow rowData = row.getItem();
					System.out.println("Double Clicked " + rowData.getTableStockCode());
				}
			});
			return row;
		});
		
		updatePortfolioTable();
		
		table.setItems(tableList);
				
		VBox portfolioOptionsBox = new VBox();
		portfolioOptionsBox.setAlignment(Pos.CENTER);
		portfolioOptionsBox.setPadding(new Insets(0,50,0,30));
		
		Button portfolioSellButton = new Button("Sell selected stocks");
		portfolioSellButton.setMinSize(50, 50);
		portfolioSellButton.setOnAction(e-> portfolioSellClicked(e));
		portfolioOptionsBox.getChildren().add(portfolioSellButton);
		
		portfolioBorder.setRight(portfolioOptionsBox);
	}
	
	public static void updatePortfolioTable(){
		String stockCode;
		String[] shareSplit;
		int numberOwned;
		
		while (tableList.size() != 0){
			tableList.remove(0);
		}
		
		for(int i = 0; i < AsxGame.activePlayer.shares.size(); i++){
			shareSplit = AsxGame.activePlayer.shares.get(i).split(":");
			stockCode = shareSplit[0];
			numberOwned = Integer.parseInt(shareSplit[1]);
			tableList.add(new PortfolioTableRow(stockCode, numberOwned));
			System.out.println(tableList.get(i).getTableStockCode());
			System.out.println(tableList.get(i).getTablePrice());
			System.out.println(tableList.get(i).getTableStockName());
			System.out.println(tableList.get(i).getTableNumber());
			System.out.println("row added " + stockCode + " "  + numberOwned);
		}
	}
	
	public static void clearTable(){
		tableList.remove(0, tableList.size());
	}
	
	private static void portfolioSellClicked(ActionEvent e){
		PortfolioTableRow selectedRow = table.getSelectionModel().getSelectedItem();
		if (selectedRow != null){
			System.out.println("Selling " + selectedRow.getTableStockCode());
		}
	}
	
	public static class PortfolioTableRow
	{
		private final SimpleStringProperty tableStockCode;
		private final SimpleStringProperty  tableStockName;
		private final SimpleFloatProperty tablePrice;
		private final SimpleIntegerProperty tableNumber;
		
		private PortfolioTableRow(String asxCode, int number){
			this.tableStockCode = new SimpleStringProperty(asxCode);
			this.tableNumber = new SimpleIntegerProperty(number);
			float priceFloat = Game.getStockCurrentPrice(asxCode);
			this.tablePrice = new SimpleFloatProperty(priceFloat);
			String name = Game.getStockName(asxCode);
			this.tableStockName = new SimpleStringProperty(name);
		}
		
		public String getTableStockCode(){
			return tableStockCode.get();
		}
		
		public void setTableStockCode(String in){
			tableStockCode.set(in);
		}
		
		public String getTableStockName(){
			return tableStockName.get();
		}
		
		public void setTableStockName(String in){
			tableStockName.set(in);
		}
		
		public Float getTablePrice(){
			return tablePrice.getValue();
		}
		
		public void setTablePrice(float in){
			tablePrice.set(in);
		}
			
		public int getTableNumber(){
			return tableNumber.get();
		}
		
		public void setTableNumber(int in){
			tableNumber.set(in);
		}

	}
}
