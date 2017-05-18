package com.amazonaws.samples;

import java.util.ArrayList;

import org.json.JSONObject;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class UI_Mailbox {
	
	public static ArrayList<JSONObject> fullMessageList = new ArrayList<JSONObject>();
	private final static TableView<InboxTableRow> table = new TableView<>();
	private static ObservableList<InboxTableRow> tableList = FXCollections.observableArrayList();
	
	static BorderPane inboxBorder = new BorderPane();;
	
	private static TextField searchField = new TextField();
	
	static VBox inboxMessageBox = new VBox();
	static Text subjectText = new Text();
	static Text messageText = new Text();
	
	static TextField toField = new TextField();
	static TextField subjectField = new TextField();
	static TextField bodyField = new TextField();
	
	
	@SuppressWarnings("unchecked")
	public static void makeInbowWindow(){
		
		while (tableList.size() != 0){
			tableList.remove(0);
		}
		while (table.getColumns().size() != 0){
			table.getColumns().remove(0);
		}	
		while (fullMessageList.size() != 0){
			fullMessageList.remove(0);
		}
		
		TableColumn<InboxTableRow, String> idCol = 
				new TableColumn<InboxTableRow, String>("Msg ID");
		idCol.setCellValueFactory(
				new PropertyValueFactory<>("msgID"));
		
		TableColumn<InboxTableRow, String> dateCol = 
				new TableColumn<InboxTableRow, String>("Date");
		dateCol.setCellValueFactory(
				new PropertyValueFactory<>("date"));
		
		TableColumn<InboxTableRow, String> timeCol = 
				new TableColumn<InboxTableRow, String>("Time");
		timeCol.setCellValueFactory(
				new PropertyValueFactory<>("time"));
		
		TableColumn<InboxTableRow, String> senderCol = 
				new TableColumn<InboxTableRow, String>("From");
		senderCol.setCellValueFactory(
				new PropertyValueFactory<>("sender"));
		
		TableColumn<InboxTableRow, String> typeCol = 
				new TableColumn<InboxTableRow, String>("Type");
		typeCol.setCellValueFactory(
				new PropertyValueFactory<>("type"));
		
		TableColumn<InboxTableRow, String> subjectCol = 
				new TableColumn<InboxTableRow, String>("Subject");
		subjectCol.setCellValueFactory(
				new PropertyValueFactory<>("subject"));
		
		table.getColumns().addAll(idCol, dateCol, timeCol, senderCol, typeCol, subjectCol);
		
		
		for (int i = 0; i < AsxGame.activePlayer.messages.size(); i++){
			String message = Game.getMessage(AsxGame.activePlayer.messages.get(i));
			if (message != null){
				JSONObject json = new JSONObject(message);
				json.put("ID", AsxGame.activePlayer.messages.get(i));
				fullMessageList.add(json);
				System.out.println(json.toString());
			}
		}
		
		for (int i = fullMessageList.size() - 1; i >=0; i--){
			int id = fullMessageList.get(i).getInt("ID");
			String date = fullMessageList.get(i).getString("Date");
			String time = fullMessageList.get(i).getString("Time");
			String sender = fullMessageList.get(i).getString("Sender");
			String type = fullMessageList.get(i).getString("Type");
			String subject = fullMessageList.get(i).getString("Subject");
			String unreadString = fullMessageList.get(i).getString("Unread");
			boolean read = true;
			if (unreadString.equals("true")){
				read = false;
			}
			tableList.add(new InboxTableRow(id, sender, date, time, type, subject, read));
		}
		
		table.setRowFactory( tv -> new TableRow<InboxTableRow>() {
			@Override
			public void updateItem(InboxTableRow row, boolean empty) {
				super.updateItem(row, empty);
				if (row.getRead() == false){
					setStyle("-fx-background-color: lightblue");
				}
			}
		});
		
		table.setRowFactory( tv -> {
			TableRow<InboxTableRow> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				InboxTableRow rowData = row.getItem();
				if (event.getClickCount() == 2 && (!row.isEmpty())){
					System.out.println("Double Clicked " + rowData.getMsgID());
					int id = rowData.getMsgID();
					for (int i = 0; i < fullMessageList.size(); i++){
						if (fullMessageList.get(i).getInt("ID") == id){
							subjectText.setText(fullMessageList.get(i).getString("Subject"));
							messageText.setText(fullMessageList.get(i).getString("Contents"));
						}
					}
					inboxBorder.setCenter(inboxMessageBox);
				}
			});
			return row;
		});
		
		FilteredList<InboxTableRow> filteredList = new FilteredList<>(tableList, p -> true);
		
		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredList.setPredicate(InboxTableRow -> {
				// If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare stock code and stock name of every row with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (InboxTableRow.getSender().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches sender
                } else if (InboxTableRow.getDate().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches date
                } else if (InboxTableRow.getTime().toLowerCase().contains(lowerCaseFilter)) {
                	return true; // Filter matches time
                } else if (InboxTableRow.getType().toLowerCase().contains(lowerCaseFilter)) {
                	return true;// Filter matches type
                } else if (InboxTableRow.getSubject().toLowerCase().contains(lowerCaseFilter)) {
                	return true;// Filter matches subject
                }
                return false; // Does not match.
			});
		});
		
		SortedList<InboxTableRow> sortedList = new SortedList<>(filteredList);
		sortedList.comparatorProperty().bind(table.comparatorProperty());
		
		table.setItems(sortedList);
		
		VBox inboxListVBox = new VBox();
		
		inboxListVBox.getChildren().addAll(searchField, table);
		
		inboxBorder.setLeft(inboxListVBox);
		
		
		subjectText.setId("inboxMessageText");
		messageText.setId("inboxMessageText");
		inboxMessageBox.getChildren().addAll(subjectText, messageText);
		
		inboxBorder.setCenter(inboxMessageBox);
		
		VBox inboxRightBox = new VBox();
		
		Button newMessageButton = new Button("Compose Message");
		newMessageButton.setMinSize(50, 50);
		newMessageButton.setOnAction(e -> makeNewMessageWindow(e));
		
		Button backButton = new Button("Back");
		backButton.setMinSize(50, 50);
		backButton.setOnAction(e-> backButtonClicked(e));
		
		inboxRightBox.getChildren().addAll(newMessageButton, backButton);
		
		inboxBorder.setRight(inboxRightBox);
		
		inboxBorder.setId("overlayBackground");
		
		UI_MainScene.homeScreenStack.getChildren().add(inboxBorder);
	}
	
	private static void makeNewMessageWindow(ActionEvent e){
		VBox newMessageBox = new VBox();
		
		toField.setPromptText("Recipient");
		subjectField.setPromptText("Subject");
		bodyField.setPromptText("MessageBody");
		bodyField.setMinHeight(200);
		
		Button sendMessageButton = new Button("Send");
		sendMessageButton.setOnAction(f -> sendMessage(f));
		
		newMessageBox.getChildren().addAll(toField, subjectField, bodyField, sendMessageButton);
		
		inboxBorder.setCenter(newMessageBox);
		
	}
	
	private static void sendMessage(ActionEvent e){
		String sender = AsxGame.activePlayer.email;
		String recipient = toField.getText();
		String subject = subjectField.getText();
		String body = bodyField.getText();
		
		if(Game.sendMessage(sender, recipient, subject, body)){
			toField.setText("");
			subjectField.setText("");
			bodyField.setText("");
		} else {
			toField.setText("Message not sent, sorry!");
		}
		
	}
	
	private static void backButtonClicked(ActionEvent e){
		int stackSize = UI_MainScene.homeScreenStack.getChildren().size();
		UI_MainScene.homeScreenStack.getChildren().remove(stackSize -1);
		UI_MainScene.inboxWindowVis = false;
		UI_MainScene.menuInboxRect.setStyle("-fx-fill:black;");
	}
	
	public static class InboxTableRow{
		private final SimpleIntegerProperty msgID;
		private final SimpleStringProperty sender;
		private final SimpleStringProperty date;
		private final SimpleStringProperty time;
		private final SimpleStringProperty type;
		private final SimpleStringProperty subject;
		private final SimpleBooleanProperty read;
		
		private InboxTableRow(int msgID, String sender, String date, String time
				, String type, String subject, boolean read){
			this.msgID = new SimpleIntegerProperty(msgID);
			this.sender = new SimpleStringProperty(sender);
			this.date = new SimpleStringProperty(date);
			this.time = new SimpleStringProperty(time);
			this.type = new SimpleStringProperty(type);
			this.subject = new SimpleStringProperty(subject);
			this.read = new SimpleBooleanProperty(read);
		}
		
		public int getMsgID(){
			return msgID.get();
		}
		
		public void setMsgID(int in){
			msgID.set(in);
		}
		
		public String getSender(){
			return sender.get();
		}
		
		public void setSender(String in){
			sender.set(in);
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
		
		public String getType(){
			return type.get();
		}
		
		public void setType(String in){
			type.set(in);
		}
		
		public String getSubject(){
			return subject.get();
		}
		
		public void setSubject(String in){
			subject.set(in);
		}
		
		public boolean getRead(){
			System.out.println("getRead");
			return read.get();
		}
		
		public void setRead(boolean in){
			read.set(in);
		}
	}
}
