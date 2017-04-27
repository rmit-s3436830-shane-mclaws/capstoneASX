import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import java.awt.Color;


public class MyHub {

	private JFrame frame;
	private JFrame frmMyHub_1;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyHub window = new MyHub();
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
	public MyHub() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMyHub_1 = new JFrame();
		frmMyHub_1.getContentPane().setLayout(null);
		frmMyHub_1.setTitle("ASX Trading Wheels");
		frmMyHub_1.setBounds(100, 100, 732, 470);
		frmMyHub_1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMyHub_1.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("ASX Trading Wheels");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.PLAIN, 20));
		label.setBounds(6, 6, 197, 36);
		frmMyHub_1.getContentPane().add(label);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setToolTipText("My Hub");		
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"My Hub", "Account Settings", "Log Out"}));
		comboBox.setSelectedIndex(1);
		comboBox.setBounds(538, 14, 163, 27);
		frmMyHub_1.getContentPane().add(comboBox);
		
		textField = new JTextField();
		textField.setText("Search");
		textField.setForeground(Color.GRAY);
		textField.setColumns(10);
		textField.setBounds(16, 54, 696, 28);
		frmMyHub_1.getContentPane().add(textField);
	}

}