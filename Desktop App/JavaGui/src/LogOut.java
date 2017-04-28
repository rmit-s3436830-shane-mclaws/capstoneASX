import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.Panel;
import javax.swing.JScrollBar;


public class LogOut {

	private JFrame frmLogOut;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LogOut window = new LogOut();
					window.frmLogOut.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LogOut() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLogOut = new JFrame();
		frmLogOut.setTitle("ASX Trading Wheels - Log Out");
		frmLogOut.setBounds(100, 100, 824, 556);
		frmLogOut.setLocationRelativeTo(null);
		frmLogOut.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLogOut.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("ASX Trading Wheels");
		label.setBounds(263, 29, 202, 58);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.PLAIN, 20));
		frmLogOut.getContentPane().add(label);
		
		JLabel lblAreYouSure = new JLabel("Log out?");
		lblAreYouSure.setBounds(337, 99, 64, 16);
		lblAreYouSure.setHorizontalAlignment(SwingConstants.CENTER);
		frmLogOut.getContentPane().add(lblAreYouSure);
		
		JButton btnYes = new JButton("Yes");
		btnYes.setBounds(288, 127, 84, 29);
		frmLogOut.getContentPane().add(btnYes);
		
		JButton btnNo = new JButton("No");
		btnNo.setBounds(381, 127, 84, 29);
		frmLogOut.getContentPane().add(btnNo);
		
		JScrollBar scrollBar = new JScrollBar();
		scrollBar.setBounds(763, 156, 15, 355);
		frmLogOut.getContentPane().add(scrollBar);
	}
}
