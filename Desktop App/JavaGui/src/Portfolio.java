import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import java.awt.Color;


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
		frmPortfolio.setTitle("ASX Trading Wheels");
		frmPortfolio.setBounds(100, 100, 732, 470);
		frmPortfolio.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPortfolio.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("ASX Trading Wheels");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.PLAIN, 20));
		label.setBounds(6, 6, 197, 36);
		frmPortfolio.getContentPane().add(label);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setToolTipText("My Hub");		
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"My Hub", "Account Settings", "Log Out"}));
		comboBox.setSelectedIndex(1);
		comboBox.setBounds(538, 14, 163, 27);
		frmPortfolio.getContentPane().add(comboBox);
		
		textField = new JTextField();
		textField.setText("Search");
		textField.setForeground(Color.GRAY);
		textField.setColumns(10);
		textField.setBounds(16, 54, 696, 28);
		frmPortfolio.getContentPane().add(textField);
		
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
	}

}