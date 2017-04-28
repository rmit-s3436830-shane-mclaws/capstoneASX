import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JTextPane;
import java.awt.SystemColor;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class LogIn {

	private JFrame frmLogin;
	private JTextField txtUsername;
	private JPasswordField pwdPassword;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LogIn window = new LogIn();
					window.frmLogin.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LogIn() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLogin = new JFrame();
		frmLogin.setTitle("ASX Trading Wheels - Login");
		frmLogin.getContentPane().setFont(new Font("Lucida Grande", Font.ITALIC, 16));
		frmLogin.setBounds(100, 100, 741, 480);
		frmLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLogin.getContentPane().setLayout(null);
		
		JLabel lblInsertAppName = new JLabel("ASX Trading Wheels");
		lblInsertAppName.setHorizontalAlignment(SwingConstants.CENTER);
		lblInsertAppName.setFont(new Font("Arial", Font.PLAIN, 20));
		lblInsertAppName.setBounds(263, 29, 202, 58);
		frmLogin.getContentPane().add(lblInsertAppName);
		
		txtUsername = new JTextField();
		txtUsername.setHorizontalAlignment(SwingConstants.CENTER);
		txtUsername.setForeground(Color.GRAY);
		txtUsername.setText("Username or Email");
		txtUsername.setBounds(285, 139, 153, 28);
		frmLogin.getContentPane().add(txtUsername);
		txtUsername.setColumns(10);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnLogin.setBounds(310, 219, 100, 28);
		frmLogin.getContentPane().add(btnLogin);
		
		pwdPassword = new JPasswordField();
		pwdPassword.setForeground(Color.GRAY);
		pwdPassword.setHorizontalAlignment(SwingConstants.CENTER);
		pwdPassword.setText("Password");
		pwdPassword.setBounds(285, 179, 153, 28);
		frmLogin.getContentPane().add(pwdPassword);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Remember me");
		chckbxNewCheckBox.setForeground(Color.GRAY);
		chckbxNewCheckBox.setBounds(298, 354, 128, 23);
		frmLogin.getContentPane().add(chckbxNewCheckBox);
		
		JTextPane textPane = new JTextPane();
		textPane.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		textPane.setText("Sign Up");
		textPane.setForeground(Color.GRAY);
		textPane.setBackground(SystemColor.window);
		textPane.setBounds(334, 302, 59, 16);
		frmLogin.getContentPane().add(textPane);
		
		JTextPane txtpnForgotPassword = new JTextPane();
		txtpnForgotPassword.setText("Forgot password?");
		txtpnForgotPassword.setForeground(Color.GRAY);
		txtpnForgotPassword.setBackground(SystemColor.window);
		txtpnForgotPassword.setBounds(310, 382, 124, 16);
		frmLogin.getContentPane().add(txtpnForgotPassword);
	}
}