package com.amazonaws.samples;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import java.awt.Component;


public class UI_MainView {

	public JFrame frmPortfolio;
	private JLabel balanceLabel;
	private JLabel lblWelcomePlayer_1;
	private String[] tableColumns = {"Stock Code", "Name", "Qty", "Current Price", ""};
	DefaultTableModel tableModel = new DefaultTableModel(tableColumns, 0);

	public UI_MainView() {
		initialize();
	}

	private void initialize() {
		frmPortfolio = new JFrame();
		frmPortfolio.getContentPane().setLayout(null);
		updateTitle();
		frmPortfolio.setBounds(100, 100, 732, 470);
		frmPortfolio.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPortfolio.getContentPane().setLayout(null);
		frmPortfolio.setResizable(false);
		
		//Header label
		JLabel label = new JLabel("ASX Trading Wheels");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.PLAIN, 20));
		label.setBounds(6, 6, 197, 36);
		frmPortfolio.getContentPane().add(label);
		
		//logout button
		JButton btnLogout = new JButton("Log Out");
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				//unload player
				AsxGame.activePlayer = null;
				AsxGame.activePlayerLoaded = false;
				
				//hide main window, show login window
				AsxGame.loginWindow.frmLogin.setVisible(true);
				AsxGame.mainWindow.frmPortfolio.setVisible(false);
			}
		});
		btnLogout.setBounds(538, 14, 163, 27);
		frmPortfolio.getContentPane().add(btnLogout);
		
		/*textField = new JTextField();
		textField.setText("Search");
		textField.setForeground(Color.GRAY);
		textField.setColumns(10);
		textField.setBounds(16, 54, 696, 28);
		frmPortfolio.getContentPane().add(textField);*/
		
/*		lblWelcomePlayer = new JLabel("Amount Spent");
		lblWelcomePlayer.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblWelcomePlayer.setBounds(448, 94, 104, 16);
		frmPortfolio.getContentPane().add(lblWelcomePlayer);*/
		
		
		if(AsxGame.activePlayerLoaded)
		{
			balanceLabel = new JLabel("Current Balance: $" + AsxGame.activePlayer.getBalanceToString());
			lblWelcomePlayer_1 = new JLabel("Welcome " + AsxGame.activePlayer.name);
			
		}
		else if(AsxGame.activeAdminLoaded)
		{
			balanceLabel = new JLabel("Current Balance: $" + AsxGame.activeAdmin.getBalanceToString());
			lblWelcomePlayer_1 = new JLabel("Welcome " + AsxGame.activeAdmin.name);
		}
		//balance label
		balanceLabel.setBounds(250, 31, 185, 16);
		frmPortfolio.getContentPane().add(balanceLabel);
		//player name label
		lblWelcomePlayer_1.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblWelcomePlayer_1.setBounds(250, 10, 177, 16);
		frmPortfolio.getContentPane().add(lblWelcomePlayer_1);
		
		//draw owned stocks table
		updateTableData();
		JTable table = new JTable(tableModel);
		JTableHeader tableHeader = table.getTableHeader();		
		table.setBounds(16, 102, 696, 275);
		tableHeader.setBounds(16, 87, 696, 15);
		frmPortfolio.getContentPane().add(table);
		frmPortfolio.getContentPane().add(table.getTableHeader());
		
		table.getColumn("").setCellRenderer(new ButtonRenderer());
		table.getColumn("").setCellEditor(
				new ButtonEditor(new JCheckBox()));
		
		//full stock list button
		JButton viewAllStocksButton = new JButton();
		viewAllStocksButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if (AsxGame.asxLoadComplete){
					AsxGame.mainWindow.frmPortfolio.setVisible(false);
					AsxGame.stockWindow.frmStockList.setVisible(true);
				} else {
					 JOptionPane.showMessageDialog(AsxGame.mainWindow.frmPortfolio,
							  "Please wait until the ASX data has finished downloading",
							  "Please wait...", JOptionPane.DEFAULT_OPTION);
				}
			}
		});
		viewAllStocksButton.setText("View Stocks");
		viewAllStocksButton.setBounds(538, 400, 163, 27);
		frmPortfolio.getContentPane().add(viewAllStocksButton);
		
		//view leaderboard button
		JButton leadersButton = new JButton();
		leadersButton.setText("View Leaderboard");
		leadersButton.setBounds(250, 400, 163, 27);
		leadersButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				AsxGame.leadersWindow = new UI_Leaderboard();
				AsxGame.leadersWindow.frmLeaderboard.setVisible(true);
				AsxGame.mainWindow.frmPortfolio.setVisible(false);
			}
		});
		frmPortfolio.add(leadersButton);
		
		//view transaction history button
		JButton histButton = new JButton();
		histButton.setText("View your transaction history");
		histButton.setBounds(20, 400, 163, 27);
		histButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				AsxGame.transHistWindow= new UI_ViewTransHist();
				AsxGame.transHistWindow.frmTransHist.setVisible(true);
				AsxGame.mainWindow.frmPortfolio.setVisible(false);
			}
		});
		frmPortfolio.add(histButton);
	}
	
	//sets the current balance text
	public void updateCurrentBalance(){
		balanceLabel.setText("Current Balance: $" + AsxGame.activePlayer.getBalanceToString());
	}
	
	//updates the table data
	//first removes all rows from the table, then adds them back in
	public void updateTableData(){
		int firstLoop = tableModel.getRowCount();
		for (int i = 0; i < firstLoop; i++){
			tableModel.removeRow(tableModel.getRowCount() - 1);
		}
		//for all owned shares
		if(AsxGame.activePlayerLoaded)
		{
			for (int i = 0; i < AsxGame.activePlayer.shares.size(); i++){
				String[] row = new String[5]; 
				String[] stringSplit = AsxGame.activePlayer.shares.get(i).split(":");
				row[0] = stringSplit[0];
				//this adds the name and price to the table
				for (int j = 0; j < AsxGame.stockArray.size(); j++){
					if (AsxGame.stockArray.get(j).code.equals(stringSplit[0])){
						row[1] = AsxGame.stockArray.get(j).name;
						row[3] = "$" + Float.toString(AsxGame.stockArray.get(j).askPrice);
					}
				}
				row[2] = stringSplit[1];
				row[4] = "Sell: " + stringSplit[0];
				tableModel.addRow(row);
			}
		}
		else if(AsxGame.activeAdminLoaded)
		{
			for (int i = 0; i < AsxGame.activeAdmin.shares.size(); i++){
				String[] row = new String[5]; 
				String[] stringSplit = AsxGame.activeAdmin.shares.get(i).split(":");
				row[0] = stringSplit[0];
				//this adds the name and price to the table
				for (int j = 0; j < AsxGame.stockArray.size(); j++){
					if (AsxGame.stockArray.get(j).code.equals(stringSplit[0])){
						row[1] = AsxGame.stockArray.get(j).name;
						row[3] = "$" + Float.toString(AsxGame.stockArray.get(j).askPrice);
					}
				}
				row[2] = stringSplit[1];
				row[4] = "Sell: " + stringSplit[0];
				tableModel.addRow(row);
			}
		}
	}
	
	//this updates the title of the window
	public void updateTitle(){
		if (AsxGame.asxLoadComplete == true){
			frmPortfolio.setTitle("ASX Trading Wheels");
		} else {
			frmPortfolio.setTitle("ASX Trading Wheels - Loading ASX Data "
						+ AsxGame.loadCompletePercent + "%");
		}
	}	
	
}

//don't really know how this works, but it makes the buttons in the table
//don't question it
@SuppressWarnings("serial")
class ButtonEditor extends DefaultCellEditor {
	protected JButton button;

	  private String label;

	  private boolean isPushed;

	  public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//if (AsxGame.asxLoadComplete == true){
						fireEditingStopped();
					//}
				}
			});
	  }

	  public Component getTableCellEditorComponent(JTable table, Object value,
	      boolean isSelected, int row, int column) {
	    if (isSelected) {
	      button.setForeground(table.getSelectionForeground());
	      button.setBackground(table.getSelectionBackground());
	    } else {
	      button.setForeground(table.getForeground());
	      button.setBackground(table.getBackground());
	    }
	    label = (value == null) ? "" : value.toString();
	    button.setText(label);
	    isPushed = true;
	    return button;
	  }

	  //buttons action defined here
	  public Object getCellEditorValue() {
		  if (isPushed) {
			  if (AsxGame.asxLoadComplete){
				  String[] labelSplit = button.getText().split(" ");
				  if (labelSplit[0].equals("Sell:")){
					  AsxGame.sellWindow = new UI_SellWindow(labelSplit[1]);
					  AsxGame.sellWindow.frmSellPopup.setVisible(true);
				  }
				  if (labelSplit[0].equals("Buy:")){
					  AsxGame.buyWindow = new UI_BuyWindow(labelSplit[1]);
					  AsxGame.buyWindow.frmBuyPopup.setVisible(true);
				  }
				 
				
			  } else {
				  JOptionPane.showMessageDialog(AsxGame.mainWindow.frmPortfolio,
						  "Please wait until the ASX data has finished downloading",
						  "Please wait...", JOptionPane.DEFAULT_OPTION);
			  }
			
	    	//JOptionPane.showMessageDialog(button, label + ": Ouch!");
	      // System.out.println(label + ": Ouch!");
		  }
		  isPushed = false;
		  return new String(label);
	  }

	  public boolean stopCellEditing() {
		  isPushed = false;
		  return super.stopCellEditing();
	  }

	  protected void fireEditingStopped() {
		  super.fireEditingStopped();
	  }
}

@SuppressWarnings("serial")
class ButtonRenderer extends JButton implements TableCellRenderer {

	public ButtonRenderer() {
		setOpaque(true);
	}

  	public Component getTableCellRendererComponent(JTable table, Object value,
  			boolean isSelected, boolean hasFocus, int row, int column) {
  		if (isSelected) {
  			setForeground(table.getSelectionForeground());
  			setBackground(table.getSelectionBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(UIManager.getColor("Button.background"));
		}
		setText((value == null) ? "" : value.toString());
		return this;
  	}
}
