package com.amazonaws.samples;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class UI_Login{
	
	Label header, userLabel, passLabel, warningLabel, registerLabel;
	Button loginButton;	
	PasswordField pwField = new PasswordField();
	TextField usernameField = new TextField();
	
	GridPane gridpane = new GridPane();
	BorderPane border = new BorderPane(gridpane);
	public Scene scene = new Scene(border, 600, 400);
	
	public UI_Login(){
	//	gridpane.setGridLinesVisible(true);
		
		border.setId("border");
		
		gridpane.setAlignment(Pos.CENTER);
		gridpane.setHgap(10);
		gridpane.setVgap(10);
		
		
		HBox headerBox = new HBox();
		header = new Label("ASX \"Trading Wheels\"");
		header.setId("header");
		headerBox.getChildren().add(header);
		headerBox.setAlignment(Pos.TOP_CENTER);
		border.setTop(headerBox);
		
		usernameField.setPromptText("Username or Email");
		gridpane.add(usernameField, 0, 1);
		
		pwField.setPromptText("Password");
		pwField.setOnKeyPressed(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode() == KeyCode.ENTER)  {
					loginButtonClicked(null);
				}
			}
		});
		gridpane.add(pwField, 0, 3);
		
		HBox warnBox = new HBox();
		warningLabel = new Label();
		warningLabel.setAlignment(Pos.CENTER);
		warningLabel.setTextFill(Color.RED);
		warnBox.getChildren().add(warningLabel);
		warnBox.setAlignment(Pos.CENTER);
		gridpane.add(warnBox, 0, 4);
		
		HBox logBtnBox = new HBox();
		HBox regBtnBox = new HBox();
		regBtnBox.setId("regBtnBox");
		
		loginButton = new Button("Login");
		loginButton.setOnAction(e-> loginButtonClicked(e));
		
		loginButton.setPrefWidth(100);
		logBtnBox.getChildren().add(loginButton);
		logBtnBox.setAlignment(Pos.CENTER);
		
		registerLabel = new Label("Sign Up");
		registerLabel.setOnMouseClicked(e-> registerLabelClicked(e));
		registerLabel.setId("registerLabel");
		regBtnBox.getChildren().add(registerLabel);
		regBtnBox.setAlignment(Pos.CENTER);
		
		gridpane.add(logBtnBox, 0, 5);
		
		gridpane.add(regBtnBox, 0, 8);
				
	}
	
	void loginButtonClicked(ActionEvent e){
		String userName = usernameField.getText();
		String password = pwField.getText();
		if (Game.login(userName, password)){
			AsxGame.UI_MainScene = new UI_MainScene();
			AsxGame.mainStage.setScene(AsxGame.UI_MainScene.scene);
			AsxGame.UI_MainScene.scene.getStylesheets().add(
					AsxGame.class.getResource("UI_MainStyle.css").toExternalForm());
			AsxGame.mainStage.centerOnScreen();
		}
		
	}
	
	void registerLabelClicked(MouseEvent e){
		AsxGame.mainStage.setScene(AsxGame.UI_RegisterScene.scene);
	}
}
