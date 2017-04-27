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

public class UI_SellWindow {
	
	JFrame frmSellPopup;
	JLabel worth;
	JLabel newBalance;
	
	public UI_SellWindow(String asxCode) {
		initialize(asxCode);
	}
	
	private void initialize(final String asxCode) {
		frmSellPopup = new JFrame();
		frmSellPopup.getContentPane().setLayout(null);
		frmSellPopup.setBounds(100, 100, 300, 300);
		frmSellPopup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmSellPopup.setResizable(false);
		
		JLabel instructs = new JLabel();
		instructs.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		instructs.setText("Type an amount of " + asxCode + " to sell");
		instructs.setBounds(50, 25, 200, 25);
		instructs.setHorizontalAlignment(SwingConstants.CENTER);
		frmSellPopup.getContentPane().add(instructs);
		
		//currently owned number of stocks count label
		JLabel owned = new JLabel();
		owned.setText("You own: " + AsxGame.activePlayer.getShareCount(asxCode));
		owned.setBounds(50, 55, 200, 25);
		frmSellPopup.getContentPane().add(owned);
		
		//textfield of how many stocks to sell
		final JTextField txtSellField = new JTextField();
		txtSellField.setHorizontalAlignment(SwingConstants.CENTER);
		txtSellField.setBounds(100, 125, 100, 25);
		txtSellField.setText(Integer.toString(AsxGame.activePlayer.getShareCount(asxCode)));
		
		//checks for updates to the textfield
		txtSellField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e){
				sellFieldChanged();
			}
			public void insertUpdate(DocumentEvent arg0) {
				sellFieldChanged();				
			}
			public void removeUpdate(DocumentEvent arg0) {
				sellFieldChanged();
			}
			
			//this happens when changes detected
			public void sellFieldChanged(){
				String txtSellFieldText = txtSellField.getText();
				int numberToSell;
				
				//does nothing if field is empty
				if (!txtSellField.getText().equals("")){
					
					//caught if number in field is not a number
					try {
						numberToSell = Integer.parseInt(txtSellFieldText);
						
						//if the number you are selling is valid
						if (numberToSell <= AsxGame.activePlayer.getShareCount(asxCode)){
							updateWorth(asxCode, numberToSell);
						} else {
							JOptionPane.showMessageDialog(AsxGame.sellWindow.frmSellPopup,
									"You may not sell that many!");
							
							//for some reason always caught, but changes nothing?????
							try {
								txtSellField.setText("AsxGame.activePlayer.getShareCount(asxCode)");
							} catch (IllegalStateException e){}
							
						}
						
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(AsxGame.sellWindow.frmSellPopup,
								  "Please only enter a number");
						try {
							txtSellField.setText("1");
						} catch (IllegalStateException ex){}
					}
				}
			}
		});
		frmSellPopup.getContentPane().add(txtSellField);
		
		//label of how much the transaction is worth
		worth = new JLabel();
		worth.setBounds(50, 80, 250, 25);
		updateWorth(asxCode, AsxGame.activePlayer.getShareCount(asxCode));
		frmSellPopup.getContentPane().add(worth);
		
		//label of your new balance after the transaction would happen
		//NOT USED AT THE MOMENT!
		newBalance = new JLabel();
		newBalance.setBounds(50, 105, 200, 25);
		updateNewBalance(asxCode, 1);
	//	frmSellPopup.getContentPane().add(newBalance);
		
		//Sell button
		JButton sellButton = new JButton();
		sellButton.setText("Sell");
		sellButton.setBounds(25, 200, 100, 35);
		
		//button action
		sellButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				
				//if the textfield is not empty
				if (!txtSellField.getText().equals("")){
					//if stocks are successfully sold
					if (Game.sellStocks(asxCode, Integer.parseInt(txtSellField.getText()))){
						//update table and balance number on main window
						AsxGame.mainWindow.updateTableData();
						AsxGame.mainWindow.updateCurrentBalance();
						//hide and remove sellWindow
						AsxGame.sellWindow.frmSellPopup.setVisible(false);
						AsxGame.sellWindow.frmSellPopup = null;
					} else {
						AsxGame.sellWindow.frmSellPopup.setVisible(false);
						AsxGame.sellWindow.frmSellPopup = null;
						JOptionPane.showMessageDialog(AsxGame.mainWindow.frmPortfolio,
								  "ERROR SELLING STOCKS",
								  "ERROR SELLING STOCKS", JOptionPane.ERROR_MESSAGE);
					}	
				} else {
					JOptionPane.showMessageDialog(AsxGame.mainWindow.frmPortfolio,
							  "Please enter a number of stocks to sell",
							  "EMPTY is not a number", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		frmSellPopup.getContentPane().add(sellButton);
		
		//back button
		JButton backButton = new JButton();
		backButton.setText("Back");
		backButton.setBounds(160, 200, 100, 35);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				AsxGame.sellWindow.frmSellPopup.setVisible(false);
				AsxGame.sellWindow.frmSellPopup = null;
			}
		});
		frmSellPopup.getContentPane().add(backButton);
		
	}
	
	//updates the "worth" label
	private void updateWorth(String asxCode, int number){
		for (int i = 0; i < AsxGame.stockArray.size(); i++){
			if (AsxGame.stockArray.get(i).code.equals(asxCode)){
				float preFeeAmount = AsxGame.stockArray.get(i).askPrice * number;
				float brokersFee = Game.calcBrokersFeeSale(preFeeAmount);
				float saleWorth = preFeeAmount - brokersFee;
				worth.setText("This transaction is worth: $" + saleWorth);
			}
		}
	}
	
	private void updateNewBalance(String asxCode, int number){
		
	}
}
