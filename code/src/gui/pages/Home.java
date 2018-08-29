package gui.pages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import controller.GlobalListener;

/**
 * Classe che definisce la schermata di login
 * @author beato
 *
 */
public class Home extends JPanel {
	private JTextField fieldUsername;
	private JPasswordField fieldPassword;
	private JLabel lblError;

	/**
	 * Costruttore della schermata di login
	 * @param navigazioneListener listener che sovraintende la navigazione
	 * e di conseguenza anche il meccanismo di login
	 */
	public Home(GlobalListener navigazioneListener) {

		setBackground(Color.WHITE);
		setLayout(new BorderLayout());

		JPanel gridNorthPanel = new JPanel();
		add(gridNorthPanel, BorderLayout.NORTH);
		gridNorthPanel.setBackground(new Color(30, 144, 255));
		GridLayout gl_gridNorthPanel = new GridLayout(3,3);
		gl_gridNorthPanel.setVgap(35);
		gl_gridNorthPanel.setHgap(3);
		gridNorthPanel.setLayout(gl_gridNorthPanel);

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setForeground(Color.WHITE);
		lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		gridNorthPanel.add(lblUsername);
		fieldUsername = new JTextField();
		fieldUsername.setBackground(Color.WHITE);
		gridNorthPanel.add(fieldUsername);

		lblError = new JLabel("");
		lblError.setForeground(Color.RED);
		gridNorthPanel.add(lblError);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setForeground(Color.WHITE);
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		gridNorthPanel.add(lblPassword);	
		fieldPassword = new JPasswordField();
		fieldPassword.setBackground(Color.WHITE);
		gridNorthPanel.add(fieldPassword);

		//label vuota funzionale all'impaginazione
		JLabel lblVoid = new JLabel("");
		gridNorthPanel.add(lblVoid);

		JButton btnLoginNegozi = new JButton("Login Negozi");
		btnLoginNegozi.setBackground(Color.LIGHT_GRAY);
		btnLoginNegozi.addActionListener(navigazioneListener);
		gridNorthPanel.add(btnLoginNegozi);

		JButton btnLoginMagazzinieri = new JButton("Login Magazzinieri");
		btnLoginMagazzinieri.setBackground(Color.LIGHT_GRAY);
		btnLoginMagazzinieri.addActionListener(navigazioneListener);
		gridNorthPanel.add(btnLoginMagazzinieri);

		JButton btnLoginSegreteria = new JButton("Login Segreteria");
		btnLoginSegreteria.setBackground(Color.LIGHT_GRAY);
		btnLoginSegreteria.addActionListener(navigazioneListener);
		gridNorthPanel.add(btnLoginSegreteria);

	}

	public String getUsername() {return fieldUsername.getText();}
	public String getPassword() {return new String(fieldPassword.getPassword());}
	public void setMessaggioErrore(String msg) {this.lblError.setText(msg);}
	/**
	 * Metodo che resetta i campi username e password impostando una stringa vuota
	 */
	public void pulisciCampi() {this.fieldUsername.setText("");this.fieldPassword.setText("");}

}
