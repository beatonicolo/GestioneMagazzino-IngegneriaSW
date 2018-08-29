package gui.pages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import controller.MagazziniereListener;
import gui.resources.BasicPanel;

/**
 * Classe che definisce la schermata di cambio posizione di un articolo all'interno del magazzino
 * @author beato
 *
 */
public class Spostamento extends BasicPanel {

	private JTextField fieldCodArticolo;
	private JTextField fieldNuovaPosizione;
	private JLabel lblError;

	/**
	 * Costruttore classe spostamento articolo in magazzino
	 * @param magazziniereListener listener che gestisce eventi,e conseguenti operazioni, 
	 * generati dalle schermate dedicate ai negozi
	 */
	public Spostamento(MagazziniereListener magazziniereListener) {

		super(magazziniereListener);

		JLabel lblSpostamento = new JLabel("Spostamento");
		this.addToNavigationBar(lblSpostamento);

		JButton btnIngresso = new JButton("Nuovo Ingresso");
		btnIngresso.setForeground(Color.DARK_GRAY);
		btnIngresso.setBackground(Color.WHITE);
		this.addToNavigationBar(btnIngresso);

		JButton btnUscita = new JButton("Nuova Uscita");
		btnUscita.setForeground(Color.DARK_GRAY);
		btnUscita.setBackground(Color.WHITE);
		this.addToNavigationBar(btnUscita);

		//creazione pannello principale
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel gridPanel = new JPanel();
		gridPanel.setBackground(Color.WHITE);
		panel.add(gridPanel, BorderLayout.NORTH);
		gridPanel.setLayout(new GridLayout(3, 2, 3, 35));

		JLabel lblCodArticolo = new JLabel("Cod. articolo:");
		lblCodArticolo.setForeground(Color.DARK_GRAY);
		gridPanel.add(lblCodArticolo);
		lblCodArticolo.setHorizontalAlignment(SwingConstants.CENTER);

		fieldCodArticolo = new JTextField();
		gridPanel.add(fieldCodArticolo);

		JLabel lblNuovaPosizione = new JLabel("Nuova posizione:");
		lblNuovaPosizione.setForeground(Color.DARK_GRAY);
		gridPanel.add(lblNuovaPosizione);
		lblNuovaPosizione.setHorizontalAlignment(SwingConstants.CENTER);

		fieldNuovaPosizione = new JTextField();
		gridPanel.add(fieldNuovaPosizione);

		JButton btnSposta = new JButton("Sposta");
		btnSposta.addActionListener(magazziniereListener);
		gridPanel.add(btnSposta);

		lblError = new JLabel("");
		lblError.setForeground(Color.RED);
		gridPanel.add(lblError);

	}

	/**
	 * Metodo che resetta i campi di input
	 */
	public void clearAll() {
		lblError.setText("");
		fieldCodArticolo.setText("");
		fieldNuovaPosizione.setText("");
	}

	public String getCodArticolo() {return fieldCodArticolo.getText();}

	public String getNuovaPosizione() {return fieldNuovaPosizione.getText();}

	public void setLblError(String errorMsg) {this.lblError.setText(errorMsg);}
}
