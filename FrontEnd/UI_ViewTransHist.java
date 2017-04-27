package com.amazonaws.samples;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class UI_ViewTransHist {
	JFrame frmTransHist;
	private String[] tableColumns = {"Date", "Time", "ASX Code", "", "Number", "Transaction Value"};
	DefaultTableModel tableModel = new DefaultTableModel(tableColumns,0);
	
	public UI_ViewTransHist(){
		initialize();
	}
	
	private void initialize(){
		frmTransHist = new JFrame();
		frmTransHist.getContentPane().setLayout(null);
		frmTransHist.setBounds(100,100,732,470);
		frmTransHist.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmTransHist.setResizable(false);
		
		JLabel header = new JLabel("Your recent transaction history");
		header.setHorizontalAlignment(SwingConstants.CENTER);
		header.setFont(new Font("Arial", Font.PLAIN, 20));
		header.setBounds(6, 6, 300, 36);
		frmTransHist.getContentPane().add(header);
		
		//table
		updateTableData();
		JTable table = new JTable(tableModel);
		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setBounds(16,87,696,15);
		table.setBounds(16, 102, 696, 275);
		frmTransHist.getContentPane().add(tableHeader);
		frmTransHist.getContentPane().add(table);
		
		//backButton
		JButton backButton = new JButton();
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				AsxGame.mainWindow.frmPortfolio.setVisible(true);
				AsxGame.transHistWindow.frmTransHist.setVisible(false);
			}
		});
		backButton.setText("Back");
		backButton.setBounds(538, 400, 163, 27);
		frmTransHist.getContentPane().add(backButton);
	}
	
	public void updateTableData(){
		for (int i = AsxGame.activePlayer.transHistory.size() -1; i > 0 ; i--){
			String[] row = new String[6];
			row[0] = AsxGame.activePlayer.transHistory.get(i).getString("Date");
			row[1] = AsxGame.activePlayer.transHistory.get(i).getString("Time");
			row[2] = AsxGame.activePlayer.transHistory.get(i).getString("ASXCode");
			row[3] = AsxGame.activePlayer.transHistory.get(i).getString("TransType");
			int number = AsxGame.activePlayer.transHistory.get(i).getInt("Number");
			row[4] = Integer.toString(number);
			float value = (float)AsxGame.activePlayer.transHistory.get(i).getDouble("Price")
						* number;
			row[5] = Float.toString(value);
			tableModel.addRow(row);
		}
	}
	
}
