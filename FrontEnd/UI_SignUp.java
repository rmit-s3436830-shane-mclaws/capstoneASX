package com.amazonaws.samples;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JPasswordField;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import java.awt.SystemColor;


public class UI_SignUp {

	public JFrame frmSignUp;
	private JPasswordField pwdPassword;
	private JPasswordField pwdConfirmPassword;
	private JTextField txtEmail;
	private JTextField txtFirstName;
	private JTextField txtLastName;
	JTextPane loading  = new JTextPane();

	/**
	 * Create the application.
	 */
	public UI_SignUp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSignUp = new JFrame();
		frmSignUp.setTitle("ASX Trading Wheels");
		frmSignUp.setBounds(100, 100, 732, 470);
		frmSignUp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSignUp.getContentPane().setLayout(null);
		
		JLabel lblSignUpAppName = new JLabel("ASX Trading Wheels");
		lblSignUpAppName.setHorizontalAlignment(SwingConstants.CENTER);
		lblSignUpAppName.setFont(new Font("Arial", Font.PLAIN, 20));
		lblSignUpAppName.setBounds(263, 29, 202, 58);
		frmSignUp.getContentPane().add(lblSignUpAppName);
		
		pwdPassword = new JPasswordField();
		pwdPassword.setText("Password");
		pwdPassword.setHorizontalAlignment(SwingConstants.CENTER);
		pwdPassword.setForeground(Color.GRAY);
		pwdPassword.setBounds(285, 255, 153, 28);
		frmSignUp.getContentPane().add(pwdPassword);
		pwdPassword.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				pwdPassword.setText("");		//clears text in field when clicked
			}
		});
		
		pwdConfirmPassword = new JPasswordField();
		pwdConfirmPassword.setText("Confirm Password");
		pwdConfirmPassword.setHorizontalAlignment(SwingConstants.CENTER);
		pwdConfirmPassword.setForeground(Color.GRAY);
		pwdConfirmPassword.setBounds(285, 225, 153, 28);
		frmSignUp.getContentPane().add(pwdConfirmPassword);
		pwdConfirmPassword.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				pwdConfirmPassword.setText("");		//clears text in field when clicked
			}
		});
		
		txtEmail = new JTextField();
		txtEmail.setText("Email");
		txtEmail.setHorizontalAlignment(SwingConstants.CENTER);
		txtEmail.setForeground(Color.GRAY);
		txtEmail.setColumns(10);
		txtEmail.setBounds(285, 115, 153, 28);
		frmSignUp.getContentPane().add(txtEmail);
		txtEmail.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				txtEmail.setText("");		//clears text in field when clicked
			}
		});
		
		/*JCheckBox chckbxRememberMe = new JCheckBox("Remember me");
		chckbxRememberMe.setForeground(Color.GRAY);
		chckbxRememberMe.setBounds(302, 290, 121, 23);
		frmSignUp.getContentPane().add(chckbxRememberMe);*/
		
		final JCheckBox chckbxIAgreeTo = new JCheckBox("I agree to the user Terms & Conditions");
		chckbxIAgreeTo.setForeground(Color.GRAY);
		chckbxIAgreeTo.setBounds(231, 314, 275, 23);
		frmSignUp.getContentPane().add(chckbxIAgreeTo);
		
		JButton btnSignUp = new JButton("Sign Up");
		btnSignUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String password = "";
				String confirmPass = "";
				char[] passAr = pwdPassword.getPassword();
				char[] confAr = pwdConfirmPassword.getPassword();
				for (int i = 0; i < passAr.length; i++){
					password += passAr[i];
				}
				for (int i = 0; i < confAr.length; i++){
					confirmPass += confAr[i];
				}
				if (chckbxIAgreeTo.isSelected()){
					if (password.equals(confirmPass)){
						if (Game.registerPlayer(txtFirstName.getText(), txtLastName.getText(), txtEmail.getText(), password)){
							AsxGame.mainWindow = new UI_MainView();
							AsxGame.mainWindow.frmPortfolio.setVisible(true);
							AsxGame.signUpWindow.frmSignUp.setVisible(false);
						}
					}
				}
			}
		});
		btnSignUp.setBounds(314, 349, 100, 28);
		frmSignUp.getContentPane().add(btnSignUp);
		
		JTextPane txtpnAlreadyHaveAn = new JTextPane();
		txtpnAlreadyHaveAn.setText("Already have an account? Login");
		txtpnAlreadyHaveAn.setForeground(Color.GRAY);
		txtpnAlreadyHaveAn.setBackground(SystemColor.window);
		txtpnAlreadyHaveAn.setBounds(263, 389, 206, 25);
		frmSignUp.getContentPane().add(txtpnAlreadyHaveAn);
		txtpnAlreadyHaveAn.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				AsxGame.loginWindow.frmLogin.setVisible(true);
				AsxGame.signUpWindow.frmSignUp.setVisible(false);
			}
		});
		
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
		
		loading.setFont(new Font("Lucida Grande", Font.BOLD, 30));
		loading.setText("Loading ASX Data, please wait...");
		loading.setBounds(0, 0, 300, 40);
		loading.setBackground(SystemColor.window);
		frmSignUp.getContentPane().add(loading);
	}
	
	public void checkLoadState(){
		if (AsxGame.asxLoadComplete == true){
			loading.setVisible(false);
		} else {
			loading.setVisible(true);
		}
	}
}