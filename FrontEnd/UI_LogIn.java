package com.amazonaws.samples;

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
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;


public class UI_LogIn {

	JFrame frmLogin;
	private JTextField txtUsername;
	private JPasswordField pwdPassword;
	JTextPane loading  = new JTextPane();	

	public UI_LogIn() {
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	
	private void initialize() {
		frmLogin = new JFrame();
		frmLogin.setTitle("ASX Trading Wheels");
		frmLogin.getContentPane().setFont(new Font("Lucida Grande", Font.ITALIC, 16));
		frmLogin.setBounds(100, 100, 732, 470);
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
		txtUsername.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				txtUsername.setText("");		//clears text in field when clicked
			}
		});
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = txtUsername.getText();
				char[] passArray = pwdPassword.getPassword();
				String passString = "";
				for (int i = 0; i < passArray.length;i++){
					passString += passArray[i];
				}
				if (Game.login(username, passString)){
					AsxGame.mainWindow = new UI_MainView();
					AsxGame.mainWindow.frmPortfolio.setVisible(true);
					AsxGame.loginWindow.frmLogin.setVisible(false);
				}
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
		pwdPassword.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				pwdPassword.setText("");		//clears text in field when clicked
			}
		});

		JTextPane textPane = new JTextPane();
		textPane.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		textPane.setText("Sign Up");
		textPane.setForeground(Color.GRAY);
		textPane.setBackground(SystemColor.window);
		textPane.setBounds(334, 302, 59, 25);
		frmLogin.getContentPane().add(textPane);
		textPane.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				AsxGame.signUpWindow.frmSignUp.setVisible(true);
				AsxGame.loginWindow.frmLogin.setVisible(false);
			}
		});
		
		loading.setFont(new Font("Lucida Grande", Font.BOLD, 20));
		loading.setText("Loading ASX Data, please wait...");
		loading.setBounds(0, 0, 350, 30);
		loading.setBackground(null);
		frmLogin.getContentPane().add(loading);
	}
	
	public void checkLoadState(){
		if (AsxGame.asxLoadComplete == true){
			loading.setVisible(false);
		} else {
			loading.setVisible(true);
		}
	}
}