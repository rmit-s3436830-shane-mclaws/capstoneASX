package com.amazonaws.samples;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class UI_BrowseStockWindow {
	
	private final static TableView<MarketTableRow> table = new TableView<>();
	private static ObservableList<MarketTableRow> tableList = FXCollections.observableArrayList();
	static HBox tableBox = new HBox(table);
	public static boolean initComplete = false;
	
	static TextField searchField = new TextField();
	
	static String code;
	static String name;
	static float price;
	
	@SuppressWarnings("unchecked")
	public static void initBrowseStockWindow(){
		while (tableList.size() != 0){
			tableList.remove(0);
		}
		while (table.getColumns().size() != 0){
			table.getColumns().remove(0);
		}
		TableColumn<MarketTableRow, String> codeCol = 
				new TableColumn<MarketTableRow, String>("Code");
		codeCol.setCellValueFactory(
				new PropertyValueFactory<>("stockCode"));
		
		TableColumn<MarketTableRow, String> nameCol = 
				new TableColumn<MarketTableRow, String>("Name");
		nameCol.setCellValueFactory(
				new PropertyValueFactory<>("stockName"));
		
		TableColumn<MarketTableRow, String> priceCol = 
				new TableColumn<MarketTableRow, String>("Price");
		priceCol.setCellValueFactory(
				new PropertyValueFactory<>("currentPrice"));
		table.getColumns().addAll(codeCol, nameCol, priceCol);
		
		for(int i = 0; i < AsxGame.stockArray.size(); i++){
			code = AsxGame.stockArray.get(i).code;
			name = AsxGame.stockArray.get(i).name;
			price = AsxGame.stockArray.get(i).askPrice;
			tableList.add(new MarketTableRow(code, name, price));
		}
		
		table.setRowFactory( tv -> {
			TableRow<MarketTableRow> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())){
					MarketTableRow rowData = row.getItem();
					System.out.println("Double Clicked " + rowData.getStockCode());
					UI_StockDetailWindow.makeStockDetailWindow(rowData.getStockCode());
				}
			});
			return row;
		});
		
		FilteredList<MarketTableRow> filteredList = new FilteredList<>(tableList, p -> true);
		
		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredList.setPredicate(MarketTableRow -> {
				// If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare stock code and stock name of every row with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (MarketTableRow.getStockCode().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches stockCode.
                } else if (MarketTableRow.getStockName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches stock name.
                }
                return false; // Does not match.
			});
		});
		
		SortedList<MarketTableRow> sortedList = new SortedList<>(filteredList);
		
		sortedList.comparatorProperty().bind(table.comparatorProperty());
		
		table.setItems(sortedList);
		initComplete = true;
	}

	public static void makeBrowseStockWindow(){
	/*		if (!initComplete){
				GridPane pleaseWaitGrid = new GridPane();
				pleaseWaitGrid.setId("overlayBackground");
				pleaseWaitGrid.setAlignment(Pos.CENTER);
				Label pleaseWaitLabel1 = new Label("Please wait");
				pleaseWaitLabel1.setId("pleaseWaitText");
				Label pleaseWaitLabel2 = new Label("While we finish downloading");
				pleaseWaitLabel2.setId("pleaseWaitText");
				Label pleaseWaitLabel3 = new Label("from the ASX");
				pleaseWaitLabel3.setId("pleaseWaitText");
				pleaseWaitGrid.add(pleaseWaitLabel1, 0, 0);
				pleaseWaitGrid.add(pleaseWaitLabel2, 0, 1);
				pleaseWaitGrid.add(pleaseWaitLabel3, 0, 2);
				
				UI_MainScene.homeScreenStack.getChildren().add(pleaseWaitGrid);
			} else {*/
				GridPane stockListOptionsGrid = new GridPane();
				BorderPane stockListCenterBorder = new BorderPane();
				
				
				searchField.setPromptText("Search by stock name or code");
				
				Button viewStockButton = new Button("View Selected Stock");
				viewStockButton.setMinSize(50, 50);
				viewStockButton.setOnAction(e-> viewStockButtonClicked(e));
				
				Button backButton = new Button("Back");
				backButton.setMinSize(50, 50);
				backButton.setOnAction(e-> backButtonClicked(e));
				
				stockListOptionsGrid.add(viewStockButton, 0, 0);
				stockListOptionsGrid.add(backButton, 0, 1);
				
				BorderPane stockListBorder = new BorderPane();
				stockListBorder.setRight(stockListOptionsGrid);
				stockListBorder.setId("overlayBackground");
				
				tableBox.setPadding(new Insets(10,10,10,10));
				
				stockListCenterBorder.setTop(searchField);
				stockListCenterBorder.setCenter(tableBox);
				stockListBorder.setCenter(stockListCenterBorder);
						
				UI_MainScene.homeScreenStack.getChildren().add(stockListBorder);
			}
	/*}*/
	
	private static void viewStockButtonClicked(ActionEvent e){
		//TODO
		//open view stock details page
	}
	
	private static void backButtonClicked(ActionEvent e){
		int stackSize = UI_MainScene.homeScreenStack.getChildren().size();
		UI_MainScene.homeScreenStack.getChildren().remove(stackSize -1);
		UI_MainScene.browseWindowVis = false;
		UI_MainScene.menuBrowseRect.setStyle("-fx-fill:black;");
	}
	
	public static class MarketTableRow{
		private final SimpleStringProperty stockCode;
		private final SimpleStringProperty stockName;
		private final SimpleFloatProperty currentPrice;
		
		private MarketTableRow(String code, String name, float price){
			stockCode = new SimpleStringProperty(code);
			stockName = new SimpleStringProperty(name);
			currentPrice = new SimpleFloatProperty(price);
		}
		
		public String getStockCode(){
			return stockCode.get();
		}
		
		public void setStockCode(String in){
			stockCode.set(in);
		}
		
		public String getStockName(){
			return stockName.get();
		}
		
		public void setStockName(String in){
			stockName.set(in);
		}
		
		public float getCurrentPrice(){
			return currentPrice.get();
		}
		
		public void setCurrentPrice(float in){
			currentPrice.set(in);
		}
		
	}
}
