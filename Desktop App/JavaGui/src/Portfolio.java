import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.Panel;
import java.awt.ScrollPane;

import javax.swing.JSeparator;
import javax.swing.JScrollBar;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import java.awt.Label;
import javax.swing.JTextPane;
import java.awt.SystemColor;


public class Portfolio {

	private JFrame frame;
	private JFrame frmPortfolio;
	private JTextField textField;
	private JLabel lblWelcomePlayer;
	private JLabel lblCurrentBalance;
	private JLabel label_1;
	private JLabel label_2;
	private JLabel lblWelcomePlayer_1;
	private JLabel lblScore;
	private JLabel label_3;
	private JScrollBar scrollBar;
	private JSeparator separator;
	private JLabel label_4;
	private JLabel label_5;
	private JTextPane textPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Portfolio window = new Portfolio();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Portfolio() {
		initialize();
		
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmPortfolio = new JFrame();
		frmPortfolio.getContentPane().setLayout(null);
		frmPortfolio.setTitle("ASX Trading Wheels - Portfolio");
		frmPortfolio.setBounds(100, 100, 741, 480);
		frmPortfolio.setLocationRelativeTo(null);
		frmPortfolio.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPortfolio.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("ASX Trading Wheels");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.PLAIN, 20));
		label.setBounds(16, 6, 197, 36);
		frmPortfolio.getContentPane().add(label);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setToolTipText("My Hub");		
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"My Portfolio", "Leaderboards", "My History", "Account Settings", "Log Out"}));
		comboBox.setSelectedIndex(1);
		comboBox.setBounds(563, 14, 163, 27);
		frmPortfolio.getContentPane().add(comboBox);
		
		textField = new JTextField();
		textField.setText("Search");
		textField.setForeground(Color.GRAY);
		textField.setColumns(10);
		textField.setBounds(16, 54, 611, 28);
		frmPortfolio.getContentPane().add(textField);
		
		JButton btnEnter = new JButton("Enter");
		btnEnter.setBounds(626, 53, 100, 28);
		frmPortfolio.getContentPane().add(btnEnter);
		
		lblWelcomePlayer = new JLabel("Amount Spent");
		lblWelcomePlayer.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblWelcomePlayer.setBounds(448, 94, 104, 16);
		frmPortfolio.getContentPane().add(lblWelcomePlayer);
		
		lblCurrentBalance = new JLabel("Current Balance");
		lblCurrentBalance.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblCurrentBalance.setBounds(586, 94, 115, 16);
		frmPortfolio.getContentPane().add(lblCurrentBalance);
		
		label_1 = new JLabel("$300");
		label_1.setBounds(458, 122, 85, 16);
		frmPortfolio.getContentPane().add(label_1);
		
		label_2 = new JLabel("$700");
		label_2.setBounds(596, 122, 85, 16);
		frmPortfolio.getContentPane().add(label_2);
		
		lblWelcomePlayer_1 = new JLabel("Welcome Player / Admin");
		lblWelcomePlayer_1.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblWelcomePlayer_1.setBounds(26, 94, 177, 16);
		frmPortfolio.getContentPane().add(lblWelcomePlayer_1);
		
		lblScore = new JLabel("Score");
		lblScore.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblScore.setBounds(338, 94, 76, 16);
		frmPortfolio.getContentPane().add(lblScore);
		
		label_3 = new JLabel("540");
		label_3.setBounds(338, 122, 67, 16);
		frmPortfolio.getContentPane().add(label_3);
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setBounds(707, 174, 19, 231);
		frmPortfolio.getContentPane().add(scrollPane);
		
		scrollBar = new JScrollBar();
		scrollBar.setBounds(697, 174, 15, 215);
		frmPortfolio.getContentPane().add(scrollBar);
		
		JLabel lblTitle = new JLabel("Title 01");
		lblTitle.setBounds(16, 194, 177, 16);
		frmPortfolio.getContentPane().add(lblTitle);
		
		JLabel lblPrice = new JLabel("$ 1234.00");
		lblPrice.setHorizontalAlignment(SwingConstants.TRAILING);
		lblPrice.setBounds(504, 194, 177, 16);
		frmPortfolio.getContentPane().add(lblPrice);
		
		JTextPane txtpnDescription = new JTextPane();
		txtpnDescription.setEditable(false);
		txtpnDescription.setForeground(Color.GRAY);
		txtpnDescription.setBackground(SystemColor.window);
		txtpnDescription.setText("Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description");
		txtpnDescription.setBounds(16, 222, 665, 57);
		frmPortfolio.getContentPane().add(txtpnDescription);
		
		separator = new JSeparator();
		separator.setBounds(16, 291, 665, 12);
		frmPortfolio.getContentPane().add(separator);
		
		label_4 = new JLabel("Title 01");
		label_4.setBounds(16, 315, 177, 16);
		frmPortfolio.getContentPane().add(label_4);
		
		label_5 = new JLabel("$ 4567.00");
		label_5.setHorizontalAlignment(SwingConstants.TRAILING);
		label_5.setBounds(504, 315, 177, 16);
		frmPortfolio.getContentPane().add(label_5);
		
		textPane = new JTextPane();
		textPane.setText("Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description");
		textPane.setForeground(Color.GRAY);
		textPane.setEditable(false);
		textPane.setBackground(SystemColor.window);
		textPane.setBounds(16, 348, 665, 57);
		frmPortfolio.getContentPane().add(textPane);
	}

	private Container getContentPane() {
		// TODO Auto-generated method stub
		return null;
	}

	private void CreateView() {
		// TODO Auto-generated method stub
		
	}
}