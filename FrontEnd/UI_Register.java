package com.amazonaws.samples;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class UI_Register {
	
	Label header, pwWarning, fnLabel, lnLabel, emailLabel, pwLabel, confLabel;
	Button register, back;
	TextField email, fnField, lnField;
	PasswordField pwField, confirmPwField;
	
	GridPane gridpane = new GridPane();
	BorderPane border = new BorderPane(gridpane);
	public Scene scene = new Scene(border, 600, 400);
	
	public UI_Register(){
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
		
		fnLabel = new Label("First Name:");
		fnLabel.setId("descLabel");
		gridpane.add(fnLabel, 0, 0);
		
		fnField = new TextField();
		fnField.setPromptText("First Name");
		gridpane.add(fnField, 1, 0);
		
		lnLabel = new Label("Last Name:");
		lnLabel.setId("descLabel");
		gridpane.add(lnLabel, 0, 1);
		
		lnField = new TextField();
		lnField.setPromptText("Last Name");
		gridpane.add(lnField, 1, 1);
		
		emailLabel = new Label("Email Address:");
		emailLabel.setId("descLabel");
		gridpane.add(emailLabel, 0, 2);
		
		email = new TextField();
		email.setPromptText("Email Address");
		gridpane.add(email, 1, 2);
		
		pwLabel = new Label("Password:");
		pwLabel.setId("descLabel");
		gridpane.add(pwLabel, 0, 3);
		
		pwField = new PasswordField();
		pwField.setPromptText("Password");
		pwField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue){
				if (newValue == false){
					if(pwField.getText().length() < 6){
						register.setDisable(false);
						pwWarning.setText("Password must contain at least 6 characters");
					} else {
						if (pwField.getText().equals(confirmPwField.getText())){
							register.setDisable(false);
							pwWarning.setText("");
						} else if (confirmPwField.getText().equals("")){
							register.setDisable(true);
							pwWarning.setText("");
						} else {
							register.setDisable(true);
							pwWarning.setText("Passwords do not match");
						}
						
					}
				}
			}
		});
		gridpane.add(pwField, 1, 3);
		
		confLabel = new Label("Confirm Password:");
		confLabel.setId("descLabel");
		gridpane.add(confLabel, 0, 4);
		
		confirmPwField = new PasswordField();
		confirmPwField.setPromptText("Confirm Password");
		confirmPwField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue){
				if (newValue == false){
					if(pwField.getText().length() < 6){
						register.setDisable(false);
						pwWarning.setText("Password must contain at least 6 characters");
					} else {
						if (pwField.getText().equals(confirmPwField.getText())){
							register.setDisable(false);
							pwWarning.setText("");
						} else if (pwField.getText().equals("")){
							register.setDisable(true);
							pwWarning.setText("");
						} else {
							register.setDisable(true);
							pwWarning.setText("Passwords do not match");
						}
						
					}
				}
			}
		});
		gridpane.add(confirmPwField, 1, 4);
		
		HBox warnBox = new HBox();
		pwWarning = new Label();
		pwWarning.setAlignment(Pos.CENTER);
		pwWarning.setId("warningLabel");
		warnBox.getChildren().add(pwWarning);
		warnBox.setAlignment(Pos.CENTER);
		gridpane.add(warnBox, 0, 5, 2, 1);
		
		BorderPane btnBox = new BorderPane();
		HBox backBtnBox = new HBox();
		HBox regBtnBox = new HBox();
		
		register = new Button("Register");
		register.setOnAction(e-> registerButtonClicked(e));
		register.setDisable(true);
		regBtnBox.getChildren().add(register);
		regBtnBox.setAlignment(Pos.CENTER_RIGHT);
		
		back = new Button("Back");
		back.setOnAction(e-> backButtonClicked(e));
		backBtnBox.getChildren().add(back);
		backBtnBox.setAlignment(Pos.CENTER_LEFT);
		
		btnBox.setRight(regBtnBox);
		btnBox.setLeft(backBtnBox);
		
		gridpane.add(btnBox, 0, 6, 2, 1);
		
	}
	
	void backButtonClicked(ActionEvent e){
		AsxGame.mainStage.setScene(AsxGame.UI_loginScene.scene);
	}
	
	void registerButtonClicked(ActionEvent e){
		if (Game.registerPlayer(fnField.getText(), lnField.getText(), 
				email.getText(), pwField.getText())){
			AsxGame.UI_MainScene = new UI_MainScene();
			AsxGame.mainStage.setScene(AsxGame.UI_MainScene.scene);
			AsxGame.UI_MainScene.scene.getStylesheets().add(
					AsxGame.class.getResource("UI_MainStyle.css").toExternalForm());
			AsxGame.mainStage.centerOnScreen();
		} else {
			pwWarning.setText("Username unavailable!");
		}
	}	
}
