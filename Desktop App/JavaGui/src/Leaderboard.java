import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JTextField;

import java.awt.Color;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JTextPane;

import java.awt.SystemColor;

import javax.swing.JCheckBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Canvas;
import java.awt.ScrollPane;
import java.awt.Scrollbar;

public class Leaderboard {

	private JFrame frmLeaderboard;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Leaderboard window = new Leaderboard();
					window.frmLeaderboard.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Leaderboard() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLeaderboard = new JFrame();
		frmLeaderboard.setTitle("ASX Trading Wheels");
		frmLeaderboard.setBounds(100, 100, 732, 470);
		frmLeaderboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLeaderboard.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("ASX Trading Wheels");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.PLAIN, 20));
		label.setBounds(16, 6, 197, 36);
		frmLeaderboard.getContentPane().add(label);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setToolTipText("My Hub");
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"My Hub", "Account Settings", "Log Out"}));
		comboBox.setSelectedIndex(1);
		comboBox.setBounds(538, 14, 163, 27);
		frmLeaderboard.getContentPane().add(comboBox);
		
		JTextField textField = new JTextField();
		textField.setText("Search");
		textField.setForeground(Color.GRAY);
		textField.setColumns(10);
		textField.setBounds(16, 54, 696, 28);
		frmLeaderboard.getContentPane().add(textField);
		
		JLabel lblNewLabel = new JLabel("Rank");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblNewLabel.setBounds(26, 123, 65, 27);
		frmLeaderboard.getContentPane().add(lblNewLabel);
		
		JLabel lblPlayer = new JLabel("Player");
		lblPlayer.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblPlayer.setBounds(148, 123, 65, 27);
		frmLeaderboard.getContentPane().add(lblPlayer);
		
		JLabel label_1 = new JLabel("1");
		label_1.setBounds(26, 162, 85, 16);
		frmLeaderboard.getContentPane().add(label_1);
		
		JLabel label_2 = new JLabel("2");
		label_2.setBounds(26, 190, 85, 16);
		frmLeaderboard.getContentPane().add(label_2);
		
		JLabel label_3 = new JLabel("3");
		label_3.setBounds(26, 218, 85, 16);
		frmLeaderboard.getContentPane().add(label_3);
		
		JLabel label_4 = new JLabel("4");
		label_4.setBounds(26, 246, 85, 16);
		frmLeaderboard.getContentPane().add(label_4);
		
		JLabel label_5 = new JLabel("5");
		label_5.setBounds(26, 274, 85, 16);
		frmLeaderboard.getContentPane().add(label_5);
		
		JLabel label_6 = new JLabel("6");
		label_6.setBounds(26, 302, 85, 16);
		frmLeaderboard.getContentPane().add(label_6);
		
		JLabel label_7 = new JLabel("7");
		label_7.setBounds(26, 330, 85, 16);
		frmLeaderboard.getContentPane().add(label_7);
		
		JLabel label_8 = new JLabel("8");
		label_8.setBounds(26, 358, 85, 16);
		frmLeaderboard.getContentPane().add(label_8);
		
		JLabel label_10 = new JLabel("17");
		label_10.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		label_10.setBounds(26, 401, 85, 16);
		frmLeaderboard.getContentPane().add(label_10);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(148, 162, 398, 16);
		frmLeaderboard.getContentPane().add(lblUsername);
		
		JLabel label_9 = new JLabel("Username");
		label_9.setBounds(148, 190, 398, 16);
		frmLeaderboard.getContentPane().add(label_9);
		
		JLabel label_11 = new JLabel("Username");
		label_11.setBounds(148, 218, 398, 16);
		frmLeaderboard.getContentPane().add(label_11);
		
		JLabel label_12 = new JLabel("Username");
		label_12.setBounds(148, 246, 398, 16);
		frmLeaderboard.getContentPane().add(label_12);
		
		JLabel label_13 = new JLabel("Username");
		label_13.setBounds(148, 274, 398, 16);
		frmLeaderboard.getContentPane().add(label_13);
		
		JLabel label_14 = new JLabel("Username");
		label_14.setBounds(148, 302, 398, 16);
		frmLeaderboard.getContentPane().add(label_14);
		
		JLabel label_15 = new JLabel("Username");
		label_15.setBounds(148, 330, 398, 16);
		frmLeaderboard.getContentPane().add(label_15);
		
		JLabel label_16 = new JLabel("Username");
		label_16.setBounds(148, 358, 398, 16);
		frmLeaderboard.getContentPane().add(label_16);
		
		JLabel label_17 = new JLabel("Username");
		label_17.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		label_17.setBounds(148, 401, 398, 16);
		frmLeaderboard.getContentPane().add(label_17);
		
		Scrollbar scrollbar = new Scrollbar();
		scrollbar.setBounds(686, 163, 15, 211);
		frmLeaderboard.getContentPane().add(scrollbar);
		
		JLabel lblScore = new JLabel("Score");
		lblScore.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblScore.setBounds(636, 123, 65, 27);
		frmLeaderboard.getContentPane().add(lblScore);
		
		JLabel label_18 = new JLabel("1");
		label_18.setHorizontalAlignment(SwingConstants.RIGHT);
		label_18.setBounds(595, 162, 85, 16);
		frmLeaderboard.getContentPane().add(label_18);
		
		JLabel label_19 = new JLabel("1");
		label_19.setHorizontalAlignment(SwingConstants.TRAILING);
		label_19.setBounds(595, 190, 85, 16);
		frmLeaderboard.getContentPane().add(label_19);
		
		JLabel label_20 = new JLabel("1");
		label_20.setHorizontalAlignment(SwingConstants.TRAILING);
		label_20.setBounds(595, 218, 85, 16);
		frmLeaderboard.getContentPane().add(label_20);
		
		JLabel label_21 = new JLabel("1");
		label_21.setHorizontalAlignment(SwingConstants.TRAILING);
		label_21.setBounds(595, 246, 85, 16);
		frmLeaderboard.getContentPane().add(label_21);
		
		JLabel label_22 = new JLabel("1");
		label_22.setHorizontalAlignment(SwingConstants.TRAILING);
		label_22.setBounds(595, 274, 85, 16);
		frmLeaderboard.getContentPane().add(label_22);
		
		JLabel label_23 = new JLabel("1");
		label_23.setHorizontalAlignment(SwingConstants.TRAILING);
		label_23.setBounds(595, 302, 85, 16);
		frmLeaderboard.getContentPane().add(label_23);
		
		JLabel label_24 = new JLabel("1");
		label_24.setHorizontalAlignment(SwingConstants.TRAILING);
		label_24.setBounds(595, 330, 85, 16);
		frmLeaderboard.getContentPane().add(label_24);
		
		JLabel label_25 = new JLabel("1");
		label_25.setHorizontalAlignment(SwingConstants.TRAILING);
		label_25.setBounds(595, 358, 85, 16);
		frmLeaderboard.getContentPane().add(label_25);
		
		JLabel label_26 = new JLabel("1");
		label_26.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		label_26.setHorizontalAlignment(SwingConstants.TRAILING);
		label_26.setBounds(595, 401, 85, 16);
		frmLeaderboard.getContentPane().add(label_26);
		
	}
}
