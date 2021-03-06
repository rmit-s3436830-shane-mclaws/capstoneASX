import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.SwingConstants;
import javax.swing.JTextField;

import java.awt.Color;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JCheckBox;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextPane;

import java.awt.SystemColor;


public class SignUp {

	private JFrame frmSignUp;
	private JTextField txtUsername;
	private JPasswordField pwdPassword;
	private JPasswordField pwdConfirmPassword;
	private JTextField txtEmail;
	private JTextField txtFirstName;
	private JTextField txtLastName;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SignUp window = new SignUp();
					window.frmSignUp.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SignUp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSignUp = new JFrame();
		frmSignUp.setTitle("ASX Trading Wheels - Sign Up");
		frmSignUp.setBounds(100, 100, 741, 480);
		frmSignUp.setLocationRelativeTo(null);
		frmSignUp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSignUp.getContentPane().setLayout(null);
		
		JLabel lblSignUpAppName = new JLabel("ASX Trading Wheels");
		lblSignUpAppName.setHorizontalAlignment(SwingConstants.CENTER);
		lblSignUpAppName.setFont(new Font("Arial", Font.PLAIN, 20));
		lblSignUpAppName.setBounds(263, 29, 202, 58);
		frmSignUp.getContentPane().add(lblSignUpAppName);
		
		txtUsername = new JTextField();
		txtUsername.setText("Username");
		txtUsername.setHorizontalAlignment(SwingConstants.CENTER);
		txtUsername.setForeground(Color.GRAY);
		txtUsername.setColumns(10);
		txtUsername.setBounds(285, 85, 153, 28);
		frmSignUp.getContentPane().add(txtUsername);
		
		pwdPassword = new JPasswordField();
		pwdPassword.setText("Password");
		pwdPassword.setHorizontalAlignment(SwingConstants.CENTER);
		pwdPassword.setForeground(Color.GRAY);
		pwdPassword.setBounds(285, 255, 153, 28);
		frmSignUp.getContentPane().add(pwdPassword);
		
		pwdConfirmPassword = new JPasswordField();
		pwdConfirmPassword.setText("Confirm Password");
		pwdConfirmPassword.setHorizontalAlignment(SwingConstants.CENTER);
		pwdConfirmPassword.setForeground(Color.GRAY);
		pwdConfirmPassword.setBounds(285, 225, 153, 28);
		frmSignUp.getContentPane().add(pwdConfirmPassword);
		
		txtEmail = new JTextField();
		txtEmail.setText("Email");
		txtEmail.setHorizontalAlignment(SwingConstants.CENTER);
		txtEmail.setForeground(Color.GRAY);
		txtEmail.setColumns(10);
		txtEmail.setBounds(285, 115, 153, 28);
		frmSignUp.getContentPane().add(txtEmail);
		
		JCheckBox chckbxRememberMe = new JCheckBox("Remember me");
		chckbxRememberMe.setForeground(Color.GRAY);
		chckbxRememberMe.setBounds(302, 290, 121, 23);
		frmSignUp.getContentPane().add(chckbxRememberMe);
		
		JCheckBox chckbxIAgreeTo = new JCheckBox("I agree to the user Terms & Conditions");
		chckbxIAgreeTo.setForeground(Color.GRAY);
		chckbxIAgreeTo.setBounds(231, 314, 275, 23);
		frmSignUp.getContentPane().add(chckbxIAgreeTo);
		
		JButton btnSignUp = new JButton("Sign Up");
		btnSignUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (txtUsername.getText().isEmpty() || pwdPassword.getText().isEmpty() || pwdConfirmPassword.getText().isEmpty() || txtEmail.getText().isEmpty()){
					JOptionPane.showMessageDialog(null, "Please input data for all fields!");
				}
			}
		});
		btnSignUp.setBounds(314, 349, 100, 28);
		frmSignUp.getContentPane().add(btnSignUp);
		
		JTextPane txtpnAlreadyHaveAn = new JTextPane();
		txtpnAlreadyHaveAn.setText("Already have an account? Login");
		txtpnAlreadyHaveAn.setForeground(Color.GRAY);
		txtpnAlreadyHaveAn.setBackground(SystemColor.window);
		txtpnAlreadyHaveAn.setBounds(263, 389, 206, 16);
		frmSignUp.getContentPane().add(txtpnAlreadyHaveAn);
		
		txtFirstName = new JTextField();
		txtFirstName.setText("First Name");
		txtFirstName.setHorizontalAlignment(SwingConstants.CENTER);
		txtFirstName.setForeground(Color.GRAY);
		txtFirstName.setColumns(10);
		txtFirstName.setBounds(285, 145, 153, 28);
		frmSignUp.getContentPane().add(txtFirstName);
		
		txtLastName = new JTextField();
		txtLastName.setText("Last Name");
		txtLastName.setHorizontalAlignment(SwingConstants.CENTER);
		txtLastName.setForeground(Color.GRAY);
		txtLastName.setColumns(10);
		txtLastName.setBounds(285, 175, 153, 28);
		frmSignUp.getContentPane().add(txtLastName);
		
		
	}
}