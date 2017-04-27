import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.JMenuBar;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JFormattedTextField;
import javax.swing.JTextPane;
import javax.swing.JTextArea;


public class MyHistory {

	private JFrame frame;
	private JTextField txtSearch;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyHistory window = new MyHistory();
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
	public MyHistory() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		JFrame frmMyHistory = new JFrame();
		frmMyHistory.getContentPane().setLayout(null);
		frmMyHistory.setTitle("ASX Trading Wheels");
		frmMyHistory.setBounds(100, 100, 732, 470);
		frmMyHistory.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMyHistory.getContentPane().setLayout(null);
		
		JLabel lblAsxTradingWheels = new JLabel("ASX Trading Wheels");
		lblAsxTradingWheels.setHorizontalAlignment(SwingConstants.CENTER);
		lblAsxTradingWheels.setFont(new Font("Arial", Font.PLAIN, 20));
		lblAsxTradingWheels.setBounds(6, 6, 197, 36);
		frmMyHistory.getContentPane().add(lblAsxTradingWheels);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setToolTipText("My Hub");
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"My Hub", "Account Settings", "Log Out"}));
		comboBox.setSelectedIndex(1);
		comboBox.setBounds(538, 14, 163, 27);
		frmMyHistory.getContentPane().add(comboBox);
		
		txtSearch = new JTextField();
		txtSearch.setForeground(Color.GRAY);
		txtSearch.setText("Search");
		txtSearch.setBounds(16, 54, 696, 28);
		frmMyHistory.getContentPane().add(txtSearch);
		txtSearch.setColumns(10);
		
		JLabel lblHello = new JLabel("Title");
		lblHello.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblHello.setBounds(132, 128, 355, 16);
		frmMyHistory.getContentPane().add(lblHello);
		
		JLabel lblAmount = new JLabel("Amount");
		lblAmount.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblAmount.setBounds(501, 128, 65, 16);
		frmMyHistory.getContentPane().add(lblAmount);
		
		JLabel lblDate = new JLabel("Date");
		lblDate.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblDate.setBounds(26, 123, 65, 27);
		frmMyHistory.getContentPane().add(lblDate);
		
		JLabel lblAccountBalance = new JLabel("Balance");
		lblAccountBalance.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblAccountBalance.setBounds(607, 128, 103, 16);
		frmMyHistory.getContentPane().add(lblAccountBalance);
		
		JLabel lblNewLabel = new JLabel("12/05/2016");
		lblNewLabel.setBounds(26, 274, 85, 16);
		frmMyHistory.getContentPane().add(lblNewLabel);
		
		JLabel label_2 = new JLabel("12/05/2016");
		label_2.setBounds(26, 162, 85, 16);
		frmMyHistory.getContentPane().add(label_2);
		
		JLabel label_3 = new JLabel("12/05/2016");
		label_3.setBounds(25, 190, 85, 16);
		frmMyHistory.getContentPane().add(label_3);
		
		JLabel label_4 = new JLabel("12/05/2016");
		label_4.setBounds(25, 218, 85, 16);
		frmMyHistory.getContentPane().add(label_4);
		
		JLabel label_5 = new JLabel("12/05/2016");
		label_5.setBounds(25, 246, 85, 16);
		frmMyHistory.getContentPane().add(label_5);
		
		JLabel lblNewLabel_1 = new JLabel("$");
		lblNewLabel_1.setBounds(501, 190, 18, 16);
		frmMyHistory.getContentPane().add(lblNewLabel_1);
		
		JLabel label_7 = new JLabel("$");
		label_7.setBounds(501, 218, 18, 16);
		frmMyHistory.getContentPane().add(label_7);
		
		JLabel label_8 = new JLabel("$");
		label_8.setBounds(501, 246, 18, 16);
		frmMyHistory.getContentPane().add(label_8);
		
		JLabel label_9 = new JLabel("$");
		label_9.setBounds(501, 274, 18, 16);
		frmMyHistory.getContentPane().add(label_9);
		
		JLabel lblNewLabel_2 = new JLabel("New label");
		lblNewLabel_2.setBounds(511, 190, 66, 16);
		frmMyHistory.getContentPane().add(lblNewLabel_2);
		
		JLabel label_10 = new JLabel("New label");
		label_10.setBounds(511, 218, 66, 16);
		frmMyHistory.getContentPane().add(label_10);
		
		JLabel label_11 = new JLabel("New label");
		label_11.setBounds(511, 246, 66, 16);
		frmMyHistory.getContentPane().add(label_11);
		
		JLabel label_12 = new JLabel("New label");
		label_12.setBounds(511, 274, 66, 16);
		frmMyHistory.getContentPane().add(label_12);
		
		JLabel label_13 = new JLabel("$");
		label_13.setBounds(607, 190, 18, 16);
		frmMyHistory.getContentPane().add(label_13);
		
		JLabel label_14 = new JLabel("$");
		label_14.setBounds(607, 218, 18, 16);
		frmMyHistory.getContentPane().add(label_14);
		
		JLabel label_15 = new JLabel("$");
		label_15.setBounds(607, 246, 18, 16);
		frmMyHistory.getContentPane().add(label_15);
		
		JLabel label_16 = new JLabel("$");
		label_16.setBounds(607, 274, 18, 16);
		frmMyHistory.getContentPane().add(label_16);
		
		JLabel label_17 = new JLabel("$");
		label_17.setBounds(607, 162, 18, 16);
		frmMyHistory.getContentPane().add(label_17);
		
		JLabel label_18 = new JLabel("New label");
		label_18.setBounds(617, 162, 61, 16);
		frmMyHistory.getContentPane().add(label_18);
		
		JLabel label_19 = new JLabel("New label");
		label_19.setBounds(617, 190, 61, 16);
		frmMyHistory.getContentPane().add(label_19);
		
		JLabel label_20 = new JLabel("New label");
		label_20.setBounds(617, 218, 61, 16);
		frmMyHistory.getContentPane().add(label_20);
		
		JLabel label_21 = new JLabel("New label");
		label_21.setBounds(617, 246, 61, 16);
		frmMyHistory.getContentPane().add(label_21);
		
		JLabel label_22 = new JLabel("New label");
		label_22.setBounds(617, 274, 61, 16);
		frmMyHistory.getContentPane().add(label_22);
		
		JLabel label_6 = new JLabel("$");
		label_6.setBounds(501, 162, 18, 16);
		frmMyHistory.getContentPane().add(label_6);
		
		JLabel label_23 = new JLabel("New label");
		label_23.setBounds(511, 162, 66, 16);
		frmMyHistory.getContentPane().add(label_23);
		
		JLabel lblStock = new JLabel("Stock 01");
		lblStock.setBounds(132, 162, 355, 16);
		frmMyHistory.getContentPane().add(lblStock);
		
		JLabel label = new JLabel("Stock 01");
		label.setBounds(132, 190, 355, 16);
		frmMyHistory.getContentPane().add(label);
		
		JLabel label_1 = new JLabel("Stock 01");
		label_1.setBounds(132, 218, 355, 16);
		frmMyHistory.getContentPane().add(label_1);
		
		JLabel label_24 = new JLabel("Stock 01");
		label_24.setBounds(132, 246, 355, 16);
		frmMyHistory.getContentPane().add(label_24);
		
		JLabel label_25 = new JLabel("Stock 01");
		label_25.setBounds(132, 274, 355, 16);
		frmMyHistory.getContentPane().add(label_25);
		
	}
}