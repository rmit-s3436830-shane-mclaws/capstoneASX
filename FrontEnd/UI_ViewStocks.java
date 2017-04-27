package com.amazonaws.samples;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class UI_ViewStocks {
	
	public JFrame frmStockList;
	private String[] tableColumns = {"Stock Code", "Name", "Current Price", ""};
	DefaultTableModel tableModel = new DefaultTableModel(tableColumns,0);
	
	public UI_ViewStocks(){
		initialize();
	}
	
	private void initialize() {
		frmStockList = new JFrame();
		frmStockList.getContentPane().setLayout(null);
		frmStockList.setBounds(100,100,732,470);
		frmStockList.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmStockList.setResizable(false);
		
		//Header Label
		JLabel header = new JLabel("All currently available ASX Stocks");
		header.setHorizontalAlignment(SwingConstants.CENTER);
		header.setFont(new Font("Arial", Font.PLAIN, 20));
		header.setBounds(6, 6, 300, 36);
		frmStockList.getContentPane().add(header);
		
		//table
		updateTableData();
		JTable table = new JTable(tableModel);
		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setBounds(16,87,696,15);
		table.setBounds(16, 102, 696, 275);
		
		frmStockList.add(tableHeader);
		frmStockList.add(table);
		table.getColumn("").setCellRenderer(new ButtonRenderer());
		table.getColumn("").setCellEditor(
				new ButtonEditor(new JCheckBox()));
		
		//back button
		JButton backButton = new JButton();
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				AsxGame.mainWindow.frmPortfolio.setVisible(true);
				AsxGame.stockWindow.frmStockList.setVisible(false);
			}
		});
		backButton.setText("Back");
		backButton.setBounds(538, 400, 163, 27);
		frmStockList.getContentPane().add(backButton);
	}
	
	public void updateTableData(){
		for (int i = 0; i < AsxGame.stockArray.size(); i++){
			String[] row = new String[4];
			row[0] = AsxGame.stockArray.get(i).code;
			row[1] = AsxGame.stockArray.get(i).name;
			row[2] = "$" + Float.toString(AsxGame.stockArray.get(i).askPrice);
			row[3] = "Buy: " + AsxGame.stockArray.get(i).code;
			tableModel.addRow(row);
		}
	}
}


