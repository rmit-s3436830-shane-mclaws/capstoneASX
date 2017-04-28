import java.awt.Canvas;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JTextField;

import java.awt.Color;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JTextPane;

import java.awt.SystemColor;

import javax.swing.JScrollBar;

public class BuySell {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BuySell window = new BuySell();
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
	public BuySell() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		JFrame frmBuySell = new JFrame();
		frmBuySell.setTitle("ASX Trading Wheels - Buy & Sell");
		frmBuySell.setBounds(100, 100, 741, 480);
		frmBuySell.setLocationRelativeTo(null);
		frmBuySell.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmBuySell.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("ASX Trading Wheels");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.PLAIN, 20));
		label.setBounds(16, 6, 197, 36);
		frmBuySell.getContentPane().add(label);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setToolTipText("My Hub");
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"My Portfolio", "Leaderboards", "My History", "Account Settings", "Log Out"}));
		comboBox.setSelectedIndex(1);
		comboBox.setBounds(563, 14, 163, 27);
		frmBuySell.getContentPane().add(comboBox);
		
		JTextField textField = new JTextField();
		textField.setText("Search");
		textField.setForeground(Color.GRAY);
		textField.setColumns(10);
		textField.setBounds(16, 54, 611, 28);
		frmBuySell.getContentPane().add(textField);
		
		JButton btnEnter = new JButton("Enter");
		btnEnter.setBounds(626, 53, 100, 28);
		frmBuySell.getContentPane().add(btnEnter);
		
		JScrollBar scrollBar = new JScrollBar();
		scrollBar.setBounds(697, 174, 15, 215);
		frmBuySell.getContentPane().add(scrollBar);
		
		JLabel lblTitle = new JLabel("Title 01");
		lblTitle.setForeground(Color.DARK_GRAY);
		lblTitle.setBounds(145, 194, 177, 16);
		frmBuySell.getContentPane().add(lblTitle);
		
		JLabel lblPrice = new JLabel("$ 1234.00");
		lblPrice.setForeground(Color.DARK_GRAY);
		lblPrice.setHorizontalAlignment(SwingConstants.TRAILING);
		lblPrice.setBounds(504, 194, 177, 16);
		frmBuySell.getContentPane().add(lblPrice);
		
		JTextPane txtpnDescription = new JTextPane();
		txtpnDescription.setEditable(false);
		txtpnDescription.setForeground(Color.GRAY);
		txtpnDescription.setBackground(SystemColor.window);
		txtpnDescription.setText("Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description");
		txtpnDescription.setBounds(145, 222, 536, 57);
		frmBuySell.getContentPane().add(txtpnDescription);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(26, 291, 655, 12);
		frmBuySell.getContentPane().add(separator);
		
		JLabel label_4 = new JLabel("Title 01");
		label_4.setForeground(Color.DARK_GRAY);
		label_4.setBounds(145, 315, 177, 16);
		frmBuySell.getContentPane().add(label_4);
		
		JLabel label_5 = new JLabel("$ 4567.00");
		label_5.setForeground(Color.DARK_GRAY);
		label_5.setHorizontalAlignment(SwingConstants.TRAILING);
		label_5.setBounds(504, 315, 177, 16);
		frmBuySell.getContentPane().add(label_5);
		
		txtpnDescription = new JTextPane();
		txtpnDescription.setText("Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description");
		txtpnDescription.setForeground(Color.GRAY);
		txtpnDescription.setEditable(false);
		txtpnDescription.setBackground(SystemColor.window);
		txtpnDescription.setBounds(145, 348, 536, 57);
		frmBuySell.getContentPane().add(txtpnDescription);
		
		Canvas canvas_1 = new Canvas();
		canvas_1.setBackground(Color.PINK);
		canvas_1.setBounds(26, 194, 104, 85);
		frmBuySell.getContentPane().add(canvas_1);
		
		Canvas canvas = new Canvas();
		canvas.setBackground(Color.YELLOW);
		canvas.setBounds(26, 309, 104, 85);
		frmBuySell.getContentPane().add(canvas);
		
		JLabel lblBrowsePurchaseSell = new JLabel("Browse. Purchase. Sell.");
		lblBrowsePurchaseSell.setForeground(Color.DARK_GRAY);
		lblBrowsePurchaseSell.setHorizontalAlignment(SwingConstants.CENTER);
		lblBrowsePurchaseSell.setFont(new Font("Arial", Font.PLAIN, 18));
		lblBrowsePurchaseSell.setBounds(16, 122, 710, 36);
		frmBuySell.getContentPane().add(lblBrowsePurchaseSell);
		
		JScrollBar scrollBar_1 = new JScrollBar();
		scrollBar_1.setBounds(711, 179, 15, 215);
		frmBuySell.getContentPane().add(scrollBar_1);
	}

}
