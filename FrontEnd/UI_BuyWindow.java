package com.amazonaws.samples;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

//THIS WHOLE THING STILL NEEDS WORK!!!
//THIS WHOLE THING STILL NEEDS WORK!!!
//THIS WHOLE THING STILL NEEDS WORK!!!
//THIS WHOLE THING STILL NEEDS WORK!!!

public class UI_BuyWindow {
	JFrame frmBuyPopup;
	JLabel worth;
	JLabel newBalance;
	
	public UI_BuyWindow(String asxCode) {
		initialize(asxCode);
	}
	
	private void initialize(final String asxCode){
		frmBuyPopup = new JFrame();
		frmBuyPopup.getContentPane().setLayout(null);
		frmBuyPopup.setBounds(100, 100, 300, 300);
		frmBuyPopup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmBuyPopup.setResizable(false);
		
		JLabel instructs = new JLabel();
		instructs.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		instructs.setText("Type an amount of " + asxCode + " to buy");
		instructs.setBounds(40, 25, 200, 25);
		instructs.setHorizontalAlignment(SwingConstants.CENTER);
		frmBuyPopup.getContentPane().add(instructs);
		
		JLabel owned = new JLabel();
		owned.setText("You own: " + AsxGame.activePlayer.getShareCount(asxCode));
		owned.setBounds(30, 55, 200, 25);
		frmBuyPopup.getContentPane().add(owned);
		
		worth = new JLabel();
		worth.setBounds(30, 80, 250, 25);
		updateWorth(asxCode, 1);
		frmBuyPopup.getContentPane().add(worth);
		
		final JTextField txtBuyField = new JTextField();
		txtBuyField.setHorizontalAlignment(SwingConstants.CENTER);
		txtBuyField.setBounds(100, 125, 100, 25);
		txtBuyField.setText("1");
		
		//this checks for changes to the text field
		txtBuyField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e){
				buyFieldChanged();
			}
			public void insertUpdate(DocumentEvent arg0) {
				buyFieldChanged();				
			}
			public void removeUpdate(DocumentEvent arg0) {
				buyFieldChanged();
			}
			
			//this is what happens when changes detected
			public void buyFieldChanged(){
				String txtBuyFieldText = txtBuyField.getText();
				int numberToBuy;
				float shareVal = 0;
				if (!txtBuyField.getText().equals("")){
					
					//caught if textfield is not number
					try {		
						numberToBuy = Integer.parseInt(txtBuyFieldText);
						for (int i = 0; i < AsxGame.stockArray.size(); i++){
							if (AsxGame.stockArray.get(i).code.equals(asxCode)){
								shareVal = AsxGame.stockArray.get(i).askPrice;
							}
						}
						
						//check that the number isnt too large
						if (numberToBuy * shareVal < AsxGame.activePlayer.balance){ 
							updateWorth(asxCode, numberToBuy);
						} else {
							JOptionPane.showMessageDialog(AsxGame.buyWindow.frmBuyPopup,
									"You may not buy that many!");
							try {
								int newTextIn = Integer.parseInt(txtBuyField.getText());
								while (numberToBuy * shareVal > AsxGame.activePlayer.balance){
									newTextIn--;
								}
								txtBuyField.setText(Integer.toString(newTextIn));
							} catch (IllegalStateException e){}
							
						}
						
						//errors if not a number
					} catch (NumberFormatException e){ 				
						JOptionPane.showMessageDialog(AsxGame.sellWindow.frmSellPopup,
								  "Please only enter a number");
						try {
							txtBuyField.setText("1");
						} catch (IllegalStateException ex){}
					}
				}
			}
		});
		frmBuyPopup.getContentPane().add(txtBuyField);
		
		//buy button
		JButton buyButton = new JButton();
		buyButton.setText("Buy");
		buyButton.setBounds(25, 200, 100, 35);
		
		//button action
		buyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				
				//if the textfield is not empty
				if (!txtBuyField.getText().equals("")){
					//if stocks are successfully sold
					if (Game.buyStocks(asxCode, Integer.parseInt(txtBuyField.getText()))){
						//update table and balance number on main window
						AsxGame.mainWindow.updateTableData();
						AsxGame.mainWindow.updateCurrentBalance();
						//hide and remove sellWindow
						AsxGame.buyWindow.frmBuyPopup.setVisible(false);
						AsxGame.buyWindow.frmBuyPopup = null;
					} else {
						AsxGame.buyWindow.frmBuyPopup.setVisible(false);
						AsxGame.buyWindow.frmBuyPopup = null;
						JOptionPane.showMessageDialog(AsxGame.mainWindow.frmPortfolio,
								  "ERROR BUYING STOCKS",
								  "ERROR BUYING STOCKS", JOptionPane.ERROR_MESSAGE);
					}	
				} else {
					JOptionPane.showMessageDialog(AsxGame.mainWindow.frmPortfolio,
							  "Please enter a number of stocks to buy",
							  "EMPTY is not a number", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		frmBuyPopup.getContentPane().add(buyButton);
		
		//backButton
		JButton backButton = new JButton();
		backButton.setText("Back");
		backButton.setBounds(160, 200, 100, 35);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				AsxGame.buyWindow.frmBuyPopup.setVisible(false);
				AsxGame.buyWindow.frmBuyPopup = null;
			}
		});
		frmBuyPopup.getContentPane().add(backButton);
			
	}
	
	//updates the worth label with how much it'll cost
	private void updateWorth(String asxCode, int number){
		for (int i = 0; i < AsxGame.stockArray.size(); i++){
			if (AsxGame.stockArray.get(i).code.equals(asxCode)){
				float preFeeAmount = AsxGame.stockArray.get(i).askPrice * number;
				float brokersFee = Game.calcBrokersFeePurch(preFeeAmount);
				float purchWorth = preFeeAmount + brokersFee;
				worth.setText("This transaction is will cost: $" + purchWorth);
			}
		}
	}
}
