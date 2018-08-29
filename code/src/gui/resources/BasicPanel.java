package gui.resources;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.SystemColor;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import controller.GlobalListener;

/**
 * Classe che definisce il pannello (Barra superiore) di welcome  e che "incapsula" la schermata richiesta
 * @author beato
 *
 */
public class BasicPanel extends JPanel {

	private JMenuBar menuBarNavigation;
	private GlobalListener navigazioneListener;

	/**
	 * Costruttore classe della barra welcome
	 * @param navigazioneListener listener che gestisce login/out e navigazione
	 */
	public BasicPanel(GlobalListener navigazioneListener) {

		this.navigazioneListener=navigazioneListener;
		
		setLayout(new BorderLayout(0, 0));

		JMenuBar menuBarWelcome = new JMenuBar();
		menuBarWelcome.setBackground(new Color(30, 144, 255));
		

		JLabel lblWelcome = new JLabel("Benvenuto!");
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcome.setForeground(SystemColor.text);
		menuBarWelcome.add(lblWelcome);

		JButton btnLogout = new JButton("Logout");
		btnLogout.setBackground(SystemColor.activeCaption);
		btnLogout.addActionListener(navigazioneListener);
		menuBarWelcome.add(btnLogout);

		menuBarNavigation = new JMenuBar();
		menuBarNavigation.setBackground(Color.LIGHT_GRAY);
		
		JPanel gridMenu = new JPanel();
		gridMenu.setLayout(new GridLayout(2,1));
		gridMenu.add(menuBarWelcome);
		gridMenu.add(menuBarNavigation);

		this.add(gridMenu,BorderLayout.NORTH);
	}
	
	protected void addToNavigationBar(Component component) {
		if (component instanceof JButton) {
			JButton button = (JButton)component;
			button.addActionListener(navigazioneListener);
			}
		menuBarNavigation.add(component);
	}

}
