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
		frmLogOut.setBounds(100, 100, 741, 480);
		frmLogOut.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLogOut.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("ASX Trading Wheels");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.PLAIN, 20));
		label.setBounds(263, 29, 202, 58);
		frmLogOut.getContentPane().add(label);
		
		JLabel lblAreYouSure = new JLabel("Log out?");
		lblAreYouSure.setHorizontalAlignment(SwingConstants.CENTER);
		lblAreYouSure.setBounds(327, 140, 64, 16);
		frmLogOut.getContentPane().add(lblAreYouSure);
		
		JLabel lblNewLabel = new JLabel("Yes");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(263, 179, 61, 16);
		frmLogOut.getContentPane().add(lblNewLabel);
		
		JLabel lblNo = new JLabel("No");
		lblNo.setHorizontalAlignment(SwingConstants.CENTER);
		lblNo.setBounds(404, 179, 61, 16);
		frmLogOut.getContentPane().add(lblNo);
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
