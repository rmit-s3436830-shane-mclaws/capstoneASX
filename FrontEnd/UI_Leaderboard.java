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

public class UI_Leaderboard {
	JFrame frmLeaderboard;
	private String[] tableColumns = {"Position", "Name", "Score"};
	DefaultTableModel tableModel = new DefaultTableModel(tableColumns,0);
	
	public UI_Leaderboard(){
		initialize();
	}
	
	private void initialize(){
		Game.getValueLeaderboard();
		frmLeaderboard = new JFrame();
		frmLeaderboard.getContentPane().setLayout(null);
		frmLeaderboard.setBounds(100,100,732,470);
		frmLeaderboard.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmLeaderboard.setResizable(false);
		
		JLabel header = new JLabel("Leaderboard - Top 10");
		header.setHorizontalAlignment(SwingConstants.CENTER);
		header.setFont(new Font("Arial", Font.PLAIN, 20));
		header.setBounds(6, 6, 300, 36);
		frmLeaderboard.getContentPane().add(header);
		
		//table
		updateTableData();
		JTable table = new JTable(tableModel);
		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setBounds(16,87,696,15);
		table.setBounds(16, 102, 696, 275);
		frmLeaderboard.getContentPane().add(tableHeader);
		frmLeaderboard.getContentPane().add(table);
		
		//backButton
		JButton backButton = new JButton();
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				AsxGame.mainWindow.frmPortfolio.setVisible(true);
				AsxGame.leadersWindow.frmLeaderboard.setVisible(false);
			}
		});
		backButton.setText("Back");
		backButton.setBounds(538, 400, 163, 27);
		frmLeaderboard.getContentPane().add(backButton);
	}
	
	public void updateTableData(){
		int boardNum = 1;
		for (int i = 0; i < AsxGame.leaderboard.size() && i < 10; i++){
			String[] row = new String[3];
			row[0] = Integer.toString(boardNum);
			String name = AsxGame.leaderboard.get(i).getString("Name") 
							+ AsxGame.leaderboard.get(i).getString("Surname");
			row[1] = name;
			row[2] = AsxGame.leaderboard.get(i).getString("Score");
			tableModel.addRow(row);
			boardNum++;
		}
	}
	
}
