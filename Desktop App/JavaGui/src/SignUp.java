import java.awt.EventQueue;

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
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import java.awt.SystemColor;


public class SignUp {

	private JFrame frmSignUp;
	private JTextField txtUsername;
	private JPasswordField pwdPassword;
	private JPasswordField pwdConfirmPassword;
	private JTextField txtEmail;

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
		frmSignUp.setTitle("ASX Trading Wheels");
		frmSignUp.setBounds(100, 100, 732, 470);
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
		txtUsername.setBounds(285, 99, 153, 28);
		frmSignUp.getContentPane().add(txtUsername);
		
		pwdPassword = new JPasswordField();
		pwdPassword.setText("Password");
		pwdPassword.setHorizontalAlignment(SwingConstants.CENTER);
		pwdPassword.setForeground(Color.GRAY);
		pwdPassword.setBounds(285, 179, 153, 28);
		frmSignUp.getContentPane().add(pwdPassword);
		
		pwdConfirmPassword = new JPasswordField();
		pwdConfirmPassword.setText("Confirm Password");
		pwdConfirmPassword.setHorizontalAlignment(SwingConstants.CENTER);
		pwdConfirmPassword.setForeground(Color.GRAY);
		pwdConfirmPassword.setBounds(285, 219, 153, 28);
		frmSignUp.getContentPane().add(pwdConfirmPassword);
		
		txtEmail = new JTextField();
		txtEmail.setText("Email");
		txtEmail.setHorizontalAlignment(SwingConstants.CENTER);
		txtEmail.setForeground(Color.GRAY);
		txtEmail.setColumns(10);
		txtEmail.setBounds(285, 139, 153, 28);
		frmSignUp.getContentPane().add(txtEmail);
		
		JCheckBox chckbxRememberMe = new JCheckBox("Remember me");
		chckbxRememberMe.setForeground(Color.GRAY);
		chckbxRememberMe.setBounds(301, 275, 121, 23);
		frmSignUp.getContentPane().add(chckbxRememberMe);
		
		JCheckBox chckbxIAgreeTo = new JCheckBox("I agree to the user Terms & Conditions");
		chckbxIAgreeTo.setForeground(Color.GRAY);
		chckbxIAgreeTo.setBounds(231, 298, 275, 23);
		frmSignUp.getContentPane().add(chckbxIAgreeTo);
		
		JButton btnSignUp = new JButton("Sign Up");
		btnSignUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
		
		
	}
}