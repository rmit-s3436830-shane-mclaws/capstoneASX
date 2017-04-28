import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JSeparator;
import javax.swing.JScrollBar;
import javax.swing.JButton;


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
		frmMyHistory.setTitle("ASX Trading Wheels - My History");
		frmMyHistory.setBounds(100, 100, 741, 480);
		frmMyHistory.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMyHistory.getContentPane().setLayout(null);
		
		JLabel lblAsxTradingWheels = new JLabel("ASX Trading Wheels");
		lblAsxTradingWheels.setHorizontalAlignment(SwingConstants.CENTER);
		lblAsxTradingWheels.setFont(new Font("Arial", Font.PLAIN, 20));
		lblAsxTradingWheels.setBounds(16, 6, 197, 36);
		frmMyHistory.getContentPane().add(lblAsxTradingWheels);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setToolTipText("My Hub");
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"My Portfolio", "Leaderboards", "My History", "Account Settings", "Log Out"}));
		comboBox.setSelectedIndex(1);
		comboBox.setBounds(563, 14, 163, 27);
		frmMyHistory.getContentPane().add(comboBox);
		
		txtSearch = new JTextField();
		txtSearch.setForeground(Color.GRAY);
		txtSearch.setText("Search");
		txtSearch.setBounds(16, 54, 610, 28);
		frmMyHistory.getContentPane().add(txtSearch);
		txtSearch.setColumns(10);
		
		JLabel lblHello = new JLabel("Title");
		lblHello.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblHello.setBounds(167, 131, 119, 10);
		frmMyHistory.getContentPane().add(lblHello);
		
		JLabel lblAmount = new JLabel("Amount");
		lblAmount.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblAmount.setBounds(572, 128, 65, 16);
		frmMyHistory.getContentPane().add(lblAmount);
		
		JLabel lblDate = new JLabel("Date");
		lblDate.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblDate.setBounds(26, 127, 35, 18);
		frmMyHistory.getContentPane().add(lblDate);
		
		JLabel lblAccountBalance = new JLabel("Balance");
		lblAccountBalance.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblAccountBalance.setBounds(649, 128, 52, 16);
		frmMyHistory.getContentPane().add(lblAccountBalance);
		
		JLabel lblNewLabel = new JLabel("12/05");
		lblNewLabel.setForeground(Color.DARK_GRAY);
		lblNewLabel.setBounds(26, 274, 42, 16);
		frmMyHistory.getContentPane().add(lblNewLabel);
		
		JLabel label_2 = new JLabel("12/05");
		label_2.setForeground(Color.DARK_GRAY);
		label_2.setBounds(26, 162, 42, 16);
		frmMyHistory.getContentPane().add(label_2);
		
		JLabel label_3 = new JLabel("12/05");
		label_3.setForeground(Color.DARK_GRAY);
		label_3.setBounds(25, 190, 42, 16);
		frmMyHistory.getContentPane().add(label_3);
		
		JLabel label_4 = new JLabel("12/07");
		label_4.setForeground(Color.DARK_GRAY);
		label_4.setBounds(25, 218, 42, 16);
		frmMyHistory.getContentPane().add(label_4);
		
		JLabel label_5 = new JLabel("12/05");
		label_5.setForeground(Color.DARK_GRAY);
		label_5.setBounds(25, 246, 42, 16);
		frmMyHistory.getContentPane().add(label_5);
		
		JLabel label_17 = new JLabel("$");
		label_17.setForeground(Color.GRAY);
		label_17.setBounds(649, 162, 8, 16);
		frmMyHistory.getContentPane().add(label_17);
		
		JLabel label_18 = new JLabel("New label");
		label_18.setHorizontalAlignment(SwingConstants.TRAILING);
		label_18.setBounds(659, 162, 42, 16);
		frmMyHistory.getContentPane().add(label_18);
		
		JLabel label_23 = new JLabel("New label");
		label_23.setHorizontalAlignment(SwingConstants.TRAILING);
		label_23.setBounds(582, 162, 55, 16);
		frmMyHistory.getContentPane().add(label_23);
		
		JLabel lblStock = new JLabel("Stock 01");
		lblStock.setBounds(167, 162, 152, 16);
		frmMyHistory.getContentPane().add(lblStock);
		
		JLabel label = new JLabel("Stock 01");
		label.setBounds(167, 190, 154, 16);
		frmMyHistory.getContentPane().add(label);
		
		JLabel label_1 = new JLabel("Stock 01");
		label_1.setBounds(167, 218, 152, 16);
		frmMyHistory.getContentPane().add(label_1);
		
		JLabel label_24 = new JLabel("Stock 01");
		label_24.setBounds(167, 246, 154, 16);
		frmMyHistory.getContentPane().add(label_24);
		
		JLabel label_25 = new JLabel("Stock 01");
		label_25.setBounds(167, 274, 152, 16);
		frmMyHistory.getContentPane().add(label_25);
		
		JLabel lblCode = new JLabel("Code");
		lblCode.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblCode.setBounds(83, 126, 42, 21);
		frmMyHistory.getContentPane().add(lblCode);
		
		JLabel label_26 = new JLabel("10000");
		label_26.setBounds(83, 162, 42, 16);
		frmMyHistory.getContentPane().add(label_26);
		
		JLabel label_27 = new JLabel("13580");
		label_27.setBounds(83, 190, 42, 16);
		frmMyHistory.getContentPane().add(label_27);
		
		JLabel label_28 = new JLabel("37381");
		label_28.setBounds(83, 218, 42, 16);
		frmMyHistory.getContentPane().add(label_28);
		
		JLabel label_29 = new JLabel("11335");
		label_29.setBounds(83, 246, 42, 16);
		frmMyHistory.getContentPane().add(label_29);
		
		JLabel label_30 = new JLabel("43322");
		label_30.setBounds(83, 274, 42, 16);
		frmMyHistory.getContentPane().add(label_30);
		
		JLabel label_13 = new JLabel("$");
		label_13.setForeground(Color.GRAY);
		label_13.setBounds(649, 190, 8, 16);
		frmMyHistory.getContentPane().add(label_13);
		
		JLabel label_14 = new JLabel("$");
		label_14.setForeground(Color.GRAY);
		label_14.setBounds(649, 218, 8, 16);
		frmMyHistory.getContentPane().add(label_14);
		
		JLabel label_15 = new JLabel("$");
		label_15.setForeground(Color.GRAY);
		label_15.setBounds(649, 246, 8, 16);
		frmMyHistory.getContentPane().add(label_15);
		
		JLabel label_16 = new JLabel("$");
		label_16.setForeground(Color.GRAY);
		label_16.setBounds(649, 274, 8, 16);
		frmMyHistory.getContentPane().add(label_16);
		
		JLabel label_19 = new JLabel("New label");
		label_19.setHorizontalAlignment(SwingConstants.TRAILING);
		label_19.setBounds(659, 190, 42, 16);
		frmMyHistory.getContentPane().add(label_19);
		
		JLabel label_20 = new JLabel("New label");
		label_20.setHorizontalAlignment(SwingConstants.TRAILING);
		label_20.setBounds(659, 218, 42, 16);
		frmMyHistory.getContentPane().add(label_20);
		
		JLabel label_21 = new JLabel("New label");
		label_21.setHorizontalAlignment(SwingConstants.TRAILING);
		label_21.setBounds(659, 246, 42, 16);
		frmMyHistory.getContentPane().add(label_21);
		
		JLabel label_22 = new JLabel("New label");
		label_22.setHorizontalAlignment(SwingConstants.TRAILING);
		label_22.setBounds(659, 274, 42, 16);
		frmMyHistory.getContentPane().add(label_22);
		
		JLabel label_6 = new JLabel("New label");
		label_6.setHorizontalAlignment(SwingConstants.TRAILING);
		label_6.setBounds(582, 190, 55, 16);
		frmMyHistory.getContentPane().add(label_6);
		
		JLabel label_7 = new JLabel("New label");
		label_7.setHorizontalAlignment(SwingConstants.TRAILING);
		label_7.setBounds(582, 218, 55, 16);
		frmMyHistory.getContentPane().add(label_7);
		
		JLabel label_8 = new JLabel("New label");
		label_8.setHorizontalAlignment(SwingConstants.TRAILING);
		label_8.setBounds(582, 246, 55, 16);
		frmMyHistory.getContentPane().add(label_8);
		
		JLabel label_9 = new JLabel("New label");
		label_9.setHorizontalAlignment(SwingConstants.TRAILING);
		label_9.setBounds(582, 274, 55, 16);
		frmMyHistory.getContentPane().add(label_9);
		
		JLabel label_10 = new JLabel("$");
		label_10.setForeground(Color.GRAY);
		label_10.setBounds(572, 162, 8, 16);
		frmMyHistory.getContentPane().add(label_10);
		
		JLabel label_11 = new JLabel("$");
		label_11.setForeground(Color.GRAY);
		label_11.setBounds(572, 190, 8, 16);
		frmMyHistory.getContentPane().add(label_11);
		
		JLabel label_12 = new JLabel("$");
		label_12.setForeground(Color.GRAY);
		label_12.setBounds(572, 218, 8, 16);
		frmMyHistory.getContentPane().add(label_12);
		
		JLabel label_31 = new JLabel("$");
		label_31.setForeground(Color.GRAY);
		label_31.setBounds(572, 246, 8, 16);
		frmMyHistory.getContentPane().add(label_31);
		
		JLabel label_32 = new JLabel("$");
		label_32.setForeground(Color.GRAY);
		label_32.setBounds(572, 274, 8, 16);
		frmMyHistory.getContentPane().add(label_32);
		
		JLabel lblWorth = new JLabel("Worth");
		lblWorth.setHorizontalAlignment(SwingConstants.CENTER);
		lblWorth.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblWorth.setBounds(495, 128, 65, 16);
		frmMyHistory.getContentPane().add(lblWorth);
		
		JLabel lblCurentWorth = new JLabel("Currently");
		lblCurentWorth.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblCurentWorth.setBounds(418, 128, 65, 16);
		frmMyHistory.getContentPane().add(lblCurentWorth);
		
		JLabel lblBoughtSold = new JLabel("Tr Type");
		lblBoughtSold.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblBoughtSold.setBounds(341, 128, 65, 16);
		frmMyHistory.getContentPane().add(lblBoughtSold);
		
		JLabel label_34 = new JLabel("15.00");
		label_34.setHorizontalAlignment(SwingConstants.TRAILING);
		label_34.setBounds(505, 162, 52, 16);
		frmMyHistory.getContentPane().add(label_34);
		
		JLabel label_35 = new JLabel("15.00");
		label_35.setHorizontalAlignment(SwingConstants.TRAILING);
		label_35.setBounds(505, 190, 52, 16);
		frmMyHistory.getContentPane().add(label_35);
		
		JLabel label_36 = new JLabel("16.00");
		label_36.setHorizontalAlignment(SwingConstants.TRAILING);
		label_36.setBounds(505, 218, 52, 16);
		frmMyHistory.getContentPane().add(label_36);
		
		JLabel label_37 = new JLabel("20.00");
		label_37.setHorizontalAlignment(SwingConstants.TRAILING);
		label_37.setBounds(505, 246, 52, 16);
		frmMyHistory.getContentPane().add(label_37);
		
		JLabel label_38 = new JLabel("9.00");
		label_38.setHorizontalAlignment(SwingConstants.TRAILING);
		label_38.setBounds(505, 274, 52, 16);
		frmMyHistory.getContentPane().add(label_38);
		
		JLabel label_39 = new JLabel("$");
		label_39.setForeground(Color.GRAY);
		label_39.setBounds(495, 162, 8, 16);
		frmMyHistory.getContentPane().add(label_39);
		
		JLabel label_40 = new JLabel("$");
		label_40.setForeground(Color.GRAY);
		label_40.setBounds(495, 190, 8, 16);
		frmMyHistory.getContentPane().add(label_40);
		
		JLabel label_41 = new JLabel("$");
		label_41.setForeground(Color.GRAY);
		label_41.setBounds(495, 218, 8, 16);
		frmMyHistory.getContentPane().add(label_41);
		
		JLabel label_42 = new JLabel("$");
		label_42.setForeground(Color.GRAY);
		label_42.setBounds(495, 246, 8, 16);
		frmMyHistory.getContentPane().add(label_42);
		
		JLabel label_43 = new JLabel("$");
		label_43.setForeground(Color.GRAY);
		label_43.setBounds(495, 274, 8, 16);
		frmMyHistory.getContentPane().add(label_43);
		
		JLabel label_45 = new JLabel("3.00");
		label_45.setHorizontalAlignment(SwingConstants.TRAILING);
		label_45.setBounds(428, 190, 52, 16);
		frmMyHistory.getContentPane().add(label_45);
		
		JLabel label_46 = new JLabel("8.00");
		label_46.setHorizontalAlignment(SwingConstants.TRAILING);
		label_46.setBounds(428, 218, 52, 16);
		frmMyHistory.getContentPane().add(label_46);
		
		JLabel label_47 = new JLabel("4.00");
		label_47.setHorizontalAlignment(SwingConstants.TRAILING);
		label_47.setBounds(428, 246, 52, 16);
		frmMyHistory.getContentPane().add(label_47);
		
		JLabel label_48 = new JLabel("3.00");
		label_48.setHorizontalAlignment(SwingConstants.TRAILING);
		label_48.setBounds(428, 274, 52, 16);
		frmMyHistory.getContentPane().add(label_48);
		
		JLabel label_50 = new JLabel("$");
		label_50.setForeground(Color.GRAY);
		label_50.setBounds(413, 218, 8, 16);
		frmMyHistory.getContentPane().add(label_50);
		
		JLabel label_51 = new JLabel("$");
		label_51.setForeground(Color.GRAY);
		label_51.setBounds(413, 246, 8, 16);
		frmMyHistory.getContentPane().add(label_51);
		
		JLabel label_52 = new JLabel("$");
		label_52.setForeground(Color.GRAY);
		label_52.setBounds(413, 274, 8, 16);
		frmMyHistory.getContentPane().add(label_52);
		
		JLabel label_53 = new JLabel("$");
		label_53.setForeground(Color.GRAY);
		label_53.setBounds(413, 190, 8, 16);
		frmMyHistory.getContentPane().add(label_53);
		
		JLabel lblBought = new JLabel("Bought");
		lblBought.setBounds(344, 162, 55, 16);
		frmMyHistory.getContentPane().add(lblBought);
		
		JLabel lblSold = new JLabel("Sold");
		lblSold.setBounds(344, 190, 55, 16);
		frmMyHistory.getContentPane().add(lblSold);
		
		JLabel lblBought_1 = new JLabel("Bought");
		lblBought_1.setBounds(344, 218, 55, 16);
		frmMyHistory.getContentPane().add(lblBought_1);
		
		JLabel lblSold_1 = new JLabel("Sold");
		lblSold_1.setBounds(344, 246, 55, 16);
		frmMyHistory.getContentPane().add(lblSold_1);
		
		JLabel lblSold_2 = new JLabel("Sold");
		lblSold_2.setBounds(344, 274, 55, 16);
		frmMyHistory.getContentPane().add(lblSold_2);
		
		JLabel label_49 = new JLabel("$");
		label_49.setForeground(Color.GRAY);
		label_49.setBounds(413, 162, 8, 16);
		frmMyHistory.getContentPane().add(label_49);
		
		JLabel lblQty = new JLabel("Qty");
		lblQty.setHorizontalAlignment(SwingConstants.CENTER);
		lblQty.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblQty.setBounds(127, 127, 35, 18);
		frmMyHistory.getContentPane().add(lblQty);
		
		JLabel label_54 = new JLabel("3");
		label_54.setHorizontalAlignment(SwingConstants.CENTER);
		label_54.setBounds(127, 162, 28, 16);
		frmMyHistory.getContentPane().add(label_54);
		
		JLabel label_55 = new JLabel("5");
		label_55.setHorizontalAlignment(SwingConstants.CENTER);
		label_55.setBounds(127, 190, 28, 16);
		frmMyHistory.getContentPane().add(label_55);
		
		JLabel label_56 = new JLabel("2");
		label_56.setHorizontalAlignment(SwingConstants.CENTER);
		label_56.setBounds(127, 218, 28, 16);
		frmMyHistory.getContentPane().add(label_56);
		
		JLabel label_57 = new JLabel("5");
		label_57.setHorizontalAlignment(SwingConstants.CENTER);
		label_57.setBounds(127, 246, 28, 16);
		frmMyHistory.getContentPane().add(label_57);
		
		JLabel label_58 = new JLabel("3");
		label_58.setHorizontalAlignment(SwingConstants.CENTER);
		label_58.setBounds(127, 274, 28, 16);
		frmMyHistory.getContentPane().add(label_58);
		
		JLabel label_33 = new JLabel("3.00");
		label_33.setHorizontalAlignment(SwingConstants.TRAILING);
		label_33.setBounds(428, 162, 52, 16);
		frmMyHistory.getContentPane().add(label_33);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setToolTipText("My Hub");
		comboBox_1.setSelectedIndex(1);
		comboBox_1.setBounds(684, 89, 42, 27);
		frmMyHistory.getContentPane().add(comboBox_1);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(26, 312, 675, 12);
		frmMyHistory.getContentPane().add(separator);
		
		JScrollBar scrollBar = new JScrollBar();
		scrollBar.setBounds(711, 131, 15, 298);
		frmMyHistory.getContentPane().add(scrollBar);
		
		JButton btnEnter = new JButton("Enter");
		btnEnter.setBounds(626, 53, 100, 28);
		frmMyHistory.getContentPane().add(btnEnter);
		
	}
}