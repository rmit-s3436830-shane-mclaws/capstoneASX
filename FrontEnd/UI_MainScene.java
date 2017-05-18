package com.amazonaws.samples;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class UI_MainScene {
	/* Menu Bar Variables */
	VBox menuBar;
	public static Rectangle menuBrowseRect, menuHistRect
							, menuInboxRect, menuLeaderRect, menuLogoutRect;

	public static boolean browseWindowVis = false;
	public static boolean histWindowVis = false;
	public static boolean inboxWindowVis = false;
	public static boolean leaderWindowVis = false;
	
	public static boolean unreadMessages = false;
	
	static StackPane inboxStack;
	
	/* Top Bar Variables */
	BorderPane topBarBorder = new BorderPane();
	GridPane valueBox;
	VBox nameBox, centerBox;
	Label balText, shareText, totalText, balNumber, shareNumber, totalNumber,
		headerLabel1, headerLabel2,
		nameLabel, emailLabel, friendCodeLabel;
		
	BorderPane insetBorderPane = new BorderPane();
	BorderPane mainBorderPane = new BorderPane();
	public static StackPane homeScreenStack = new StackPane();
	public Scene scene = new Scene(mainBorderPane, 1280, 720);
	
	public UI_MainScene(){
		mainBorderPane.setId("mainBorderPane");
		mainBorderPane.setCenter(insetBorderPane);
		insetBorderPane.setCenter(homeScreenStack);
		makeMenuBar();
		makeTopBar();
		makeCenterView();
	}
	
	private void makeMenuBar(){
		menuBar = new VBox();
				
		StackPane browseStack = new StackPane();
		menuBrowseRect = new Rectangle();
		menuBrowseRect.setWidth(100.0f);
		menuBrowseRect.setHeight(100.0f);
		menuBrowseRect.setArcHeight(10.0);
		menuBrowseRect.setArcWidth(10.0);
		VBox browseLabelVBox = new VBox();
		browseLabelVBox.setAlignment(Pos.CENTER);
		Label buyButtonLabelBuy = new Label("Browse");
		buyButtonLabelBuy.setId("buttonLabel");
		Label buyButtonLabelStocks = new Label("Stocks");
		buyButtonLabelStocks.setId("buttonLabel");
		browseLabelVBox.getChildren().addAll(buyButtonLabelBuy, buyButtonLabelStocks);
		browseStack.getChildren().addAll(menuBrowseRect, browseLabelVBox);
		browseStack.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e){
				if (!browseWindowVis){
					menuBrowseRect.setStyle("-fx-fill:darkgray;");
				}				
			}
		});
		browseStack.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e){
				if (!browseWindowVis){
					menuBrowseRect.setStyle("-fx-fill:black;");
				}
			}
		});
		browseStack.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent e){
				if (!browseWindowVis){
					closeOpenWindows();
					UI_BrowseStockWindow.makeBrowseStockWindow();
					menuBrowseRect.setStyle("-fx-fill:darkblue;");
					browseWindowVis = true;
				} else {
					closeOpenWindows();
					menuBrowseRect.setStyle("-fx-fill:darkgray;");
				}
			}
		});
		
		StackPane histStack = new StackPane();
		menuHistRect = new Rectangle();
		menuHistRect.setWidth(100.0f);
		menuHistRect.setHeight(100.0f);
		menuHistRect.setArcHeight(10.0);
		menuHistRect.setArcWidth(10.0);
		VBox histLabelVBox = new VBox();
		histLabelVBox.setAlignment(Pos.CENTER);
		Label histButtonLabelView = new Label("View");
		histButtonLabelView.setId("buttonLabel");
		Label histButtonLabelHistory = new Label("History");
		histButtonLabelHistory.setId("buttonLabel");
		histLabelVBox.getChildren().addAll(histButtonLabelView, histButtonLabelHistory);
		histStack.getChildren().addAll(menuHistRect, histLabelVBox);
		histStack.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e){
				if (!histWindowVis){
					menuHistRect.setStyle("-fx-fill:darkgray;");
				}
			}
		});
		histStack.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e){
				if (!histWindowVis){
					menuHistRect.setStyle("-fx-fill:black;");
				}
			}
		});
		histStack.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent e){
				if (!histWindowVis){
					closeOpenWindows();
					UI_HistoryWindow.makeHistoryWindow();
					menuHistRect.setStyle("-fx-fill:darkblue;");
					histWindowVis = true;
				} else {
					closeOpenWindows();
					menuHistRect.setStyle("-fx-fill:darkgray;");
				}
			}
		});
		
		StackPane leaderboardStack = new StackPane();
		menuLeaderRect = new Rectangle();
		menuLeaderRect.setHeight(100.0f);
		menuLeaderRect.setWidth(100.0f);
		menuLeaderRect.setArcHeight(10.0);
		menuLeaderRect.setArcWidth(10.0);
		VBox leaderLabelVBox = new VBox();
		leaderLabelVBox.setAlignment(Pos.CENTER);
		Label leaderButtonLabel1 = new Label("Leader-");
		leaderButtonLabel1.setId("buttonLabel");
		Label leaderButtonLabel2 = new Label("board");
		leaderButtonLabel2.setId("buttonLabel");
		leaderLabelVBox.getChildren().addAll(leaderButtonLabel1, leaderButtonLabel2);
		leaderboardStack.getChildren().addAll(menuLeaderRect, leaderLabelVBox);
		leaderboardStack.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e){
				menuLeaderRect.setStyle("-fx-fill:darkgray;");
			}
		});
		leaderboardStack.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e){
				menuLeaderRect.setStyle("-fx-fill:black;");
			}
		});
		leaderboardStack.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent e){
				if (!leaderWindowVis){
					closeOpenWindows();
					UI_Leaderboard.makeLeaderBoardWindow();
					menuLeaderRect.setStyle("-fx-fill:darkblue;");
					leaderWindowVis = true;
				} else {
					closeOpenWindows();
					menuLeaderRect.setStyle("-fx-fill:darkgray;");
				}
			}
		});
		
		
		inboxStack = new StackPane();
		menuInboxRect = new Rectangle();
		menuInboxRect.setWidth(100.0f);
		menuInboxRect.setHeight(100.0f);
		menuInboxRect.setArcHeight(10.0);
		menuInboxRect.setArcWidth(10.0);
		VBox inboxLabelVBox = new VBox();
		inboxLabelVBox.setAlignment(Pos.CENTER);
		Label inboxButtonLabel = new Label("Inbox");
		inboxButtonLabel.setId("buttonLabel");
		inboxLabelVBox.getChildren().addAll(inboxButtonLabel);
		inboxStack.getChildren().addAll(menuInboxRect, inboxLabelVBox);
		inboxStack.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e){
				menuInboxRect.setStyle("-fx-fill:darkgray;");
			}
		});
		inboxStack.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e){
				menuInboxRect.setStyle("-fx-fill:black;");
			}
		});
		inboxStack.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent e){
				if (!inboxWindowVis){
					closeOpenWindows();
					UI_Mailbox.makeInbowWindow();
					menuInboxRect.setStyle("-fx-fill:darkblue;");
					inboxWindowVis = true;
				} else {
					closeOpenWindows();
					menuInboxRect.setStyle("-fx-fill:darkgray;");
				}
			}
		});
		updateUnreadMessages();
		
		StackPane logoutStack = new StackPane();
		menuLogoutRect = new Rectangle();
		menuLogoutRect.setWidth(100.0f);
		menuLogoutRect.setHeight(100.0f);
		menuLogoutRect.setArcHeight(10.0);
		menuLogoutRect.setArcWidth(10.0);
		VBox LogOutLabelVBox = new VBox();
		LogOutLabelVBox.setAlignment(Pos.CENTER);
		Label LogoutButtonLabel= new Label("Logout");
		LogoutButtonLabel.setId("buttonLabel");
		LogOutLabelVBox.getChildren().addAll(LogoutButtonLabel);
		logoutStack.getChildren().addAll(menuLogoutRect, LogOutLabelVBox);
		logoutStack.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e){
				menuLogoutRect.setStyle("-fx-fill:darkgray;");
			}
		});
		logoutStack.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e){
				menuLogoutRect.setStyle("-fx-fill:black;");
			}
		});
		logoutStack.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e){
				UI_Portfolio.clearTable();
				AsxGame.activePlayer = null;
				AsxGame.activePlayerLoaded = false;
				AsxGame.mainStage.setScene(AsxGame.UI_loginScene.scene);
				AsxGame.UI_MainScene = null;
			}
		});
		
		menuBar.getChildren().addAll(browseStack, histStack, leaderboardStack
										, inboxStack, logoutStack);
		
		menuBar.setAlignment(Pos.TOP_CENTER);
		menuBar.setSpacing(37);
		menuBar.setPadding(new Insets(37, 20, 37, 20));
		menuBar.setId("menuBar");
		
		mainBorderPane.setLeft(menuBar);
	}
	
	private void makeTopBar(){
		/* start value box building */
		valueBox = new GridPane();
		valueBox.setId("valueBox");
		valueBox.setPadding(new Insets(10,10,10,10));
		valueBox.setVgap(5);
		valueBox.setHgap(20);
		
		balText = new Label("BALANCE:");
		shareText = new Label("SHARE VALUE:");
		totalText = new Label("TOTAL VALUE:");
		
		balNumber = new Label("$" + AsxGame.activePlayer.getBalanceToString());
		shareNumber = new Label("$" + Float.toString(AsxGame.activePlayer.shareVal));
		totalNumber = new Label("$" + Float.toString(AsxGame.activePlayer.totalValue));
		
		valueBox.add(balText, 0, 0);
		valueBox.add(balNumber, 1, 0);
		valueBox.add(shareText, 0, 1);
		valueBox.add(shareNumber, 1, 1);
		valueBox.add(totalText, 0, 2);
		valueBox.add(totalNumber, 1, 2);
		
		/* end value box building */
		
		/* start center box building */
		centerBox = new VBox();
		
		headerLabel1 = new Label("ASX \"Trading Wheels\"");
		headerLabel1.setId("header");
		String headerLabel2String = "A project by:\n"
				+ "Shane McLaws (s3436830)\t\t"
				+ "Callum Pullyblank (s3378543)\n"
				+ "Zac Williams (s3431670)\t\t\t"
				+ "Sonia Varghese (s3484881)\n";
		headerLabel2 = new Label(headerLabel2String);
		
		centerBox.setPadding(new Insets(10,10,10,10));	
		centerBox.setAlignment(Pos.TOP_CENTER);
		
		centerBox.getChildren().addAll(headerLabel1, headerLabel2);
		
		/* end center box building */
				
		/* start name box building */
		nameBox = new VBox();
		
		nameLabel = new Label(AsxGame.activePlayer.name + " " + AsxGame.activePlayer.surname);
		emailLabel = new Label(AsxGame.activePlayer.email);
		friendCodeLabel = new Label("Friend Code: TEMPCODE");
		
		nameBox.getChildren().addAll(nameLabel, emailLabel, friendCodeLabel);
		nameBox.setId("nameBox");
		nameBox.setAlignment(Pos.TOP_RIGHT);
		nameBox.setPadding(new Insets(10,10,10,10));
		
		/* end name box building */
		
		topBarBorder.setLeft(valueBox);
		topBarBorder.setRight(nameBox);
		topBarBorder.setCenter(centerBox);
		
		insetBorderPane.setTop(topBarBorder);
	}
	
	private void closeOpenWindows(){
		while (homeScreenStack.getChildren().size() != 1){
			homeScreenStack.getChildren().remove(1);
			menuBrowseRect.setStyle("-fx-fill:black;");
			menuHistRect.setStyle("-fx-fill:black;");
			menuInboxRect.setStyle("-fx-fill:black;");
			menuLeaderRect.setStyle("-fx-fill:black;");
			menuLogoutRect.setStyle("-fx-fill:black;");
			
			browseWindowVis = false;
			histWindowVis = false;
			inboxWindowVis = false;
			leaderWindowVis = false;
		}
	}
	
	private void makeCenterView(){
		homeScreenStack.setAlignment(Pos.CENTER);
		UI_Portfolio.makePortfolioTable();	
	}

	public void updateTopBar(){
		balNumber.setText("$" + AsxGame.activePlayer.getBalanceToString());
		shareNumber.setText("$" + Float.toString(AsxGame.activePlayer.shareVal));
		totalNumber.setText("$" + Float.toString(AsxGame.activePlayer.totalValue));
	}

	public static void updateUnreadMessages(){
		if (unreadMessages == true){
			inboxStack.getChildren().remove(inboxStack.getChildren().size() -1 );
			unreadMessages = false;
		}
		if (AsxGame.activePlayer.unreadMessages.size() > 0){
			StackPane unreadMsgNotice = new StackPane();
			unreadMsgNotice.setAlignment(Pos.TOP_RIGHT);
			Rectangle unreadMsgRect = new Rectangle();
			unreadMsgRect.setWidth(20.0f);
			unreadMsgRect.setHeight(20.0f);
			unreadMsgRect.setArcHeight(10.0);
			unreadMsgRect.setArcWidth(10.0);
			unreadMsgRect.setStyle("-fx-fill:red");
			unreadMsgNotice.getChildren().add(unreadMsgRect);
			
			Label unreadMsgLabel = new Label(Integer.toString(AsxGame.activePlayer.unreadMessages.size()));
			unreadMsgLabel.setAlignment(Pos.CENTER);
			unreadMsgLabel.setId("unreadMsgLabel");
			unreadMsgNotice.getChildren().add(unreadMsgLabel);
			
			inboxStack.getChildren().add(unreadMsgNotice);
			unreadMessages = true;
			
		}
	}
}
