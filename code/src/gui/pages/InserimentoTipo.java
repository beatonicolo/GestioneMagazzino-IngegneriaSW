package gui.pages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import controller.GlobalListener;
import controller.SegreteriaListener;
import gui.resources.BasicPanel;
import model.dbcom.DataSource;

/**
 * Classe che definisce la schermata di inserimento del tipo
 * @author beato
 *
 */
public class InserimentoTipo extends BasicPanel {
	private JTextField fieldNome;
	private JTextField fieldDescrizione;
	private JComboBox <String> cbSport;
	private JTextField fieldMateriali;
	private JLabel lblError;

	/**
	 * costruttore della classe
	 * @param segreteriaListener listener che si occupa della gestione degli eventi, e le conseguenti
	 * azioni da eseguire, generati nelle schermate dedicate al personale della segreteria.
	 */
	public InserimentoTipo(SegreteriaListener segreteriaListener) {
		
		super((GlobalListener) segreteriaListener);
	
		JButton btnUscite = new JButton("Uscite");
		btnUscite.setForeground(Color.DARK_GRAY);
		btnUscite.setBackground(Color.WHITE);
		btnUscite.addActionListener((GlobalListener) segreteriaListener);
		this.addToNavigationBar(btnUscite);

		JButton btnEntrate = new JButton("Entrate");
		btnEntrate.setForeground(Color.DARK_GRAY);
		btnEntrate.setBackground(Color.WHITE);
		btnEntrate.addActionListener((GlobalListener) segreteriaListener);
		this.addToNavigationBar(btnEntrate);

		JLabel lblInserimentoTipoArticolo = new JLabel("Inserimento Tipo Articolo");
		lblInserimentoTipoArticolo.setForeground(Color.BLACK);
		this.addToNavigationBar(lblInserimentoTipoArticolo);

		//creazione del pannello contenente i componenti per l'inserimento
		JPanel centerPanel = new JPanel();
		centerPanel.setBackground(Color.WHITE);
		centerPanel.setForeground(Color.WHITE);
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));

		JPanel gridCenterPanel = new JPanel();
		gridCenterPanel.setBackground(Color.WHITE);
		centerPanel.add(gridCenterPanel, BorderLayout.NORTH);
		gridCenterPanel.setLayout(new GridLayout(5, 2, 3, 2));

		JLabel lblNome = new JLabel("Nome:");
		lblNome.setHorizontalAlignment(SwingConstants.CENTER);
		lblNome.setForeground(Color.DARK_GRAY);
		gridCenterPanel.add(lblNome);

		fieldNome = new JTextField();
		gridCenterPanel.add(fieldNome);
		fieldNome.setColumns(10);

		JLabel lblDescrizione = new JLabel("Descrizione:");
		lblDescrizione.setHorizontalAlignment(SwingConstants.CENTER);
		lblDescrizione.setForeground(Color.DARK_GRAY);
		gridCenterPanel.add(lblDescrizione);

		fieldDescrizione = new JTextField();
		gridCenterPanel.add(fieldDescrizione);
		fieldDescrizione.setColumns(10);

		JLabel lblSport = new JLabel("Sport:");
		lblSport.setHorizontalAlignment(SwingConstants.CENTER);
		lblSport.setForeground(Color.DARK_GRAY);
		gridCenterPanel.add(lblSport);

		/*definizione combobox per la scelta dello sport, chiama un metodo che legge dal database
		 * i nomi degli sport e li inserisce nella combobox
		 */
		DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<String>();
		List <String> sport= DataSource.getInstance().getListaSport();
		for (int i=0;i<sport.size();i++)
			cbModel.addElement(sport.get(i));
		cbSport = new JComboBox <String>(cbModel);
		cbSport.setSelectedIndex(0);
		gridCenterPanel.add(cbSport);

		JLabel lblMateriali = new JLabel("Materiali:");
		lblMateriali.setHorizontalAlignment(SwingConstants.CENTER);
		lblMateriali.setForeground(Color.DARK_GRAY);
		gridCenterPanel.add(lblMateriali);

		fieldMateriali = new JTextField();
		gridCenterPanel.add(fieldMateriali);
		fieldMateriali.setColumns(10);

		lblError = new JLabel("");
		lblError.setForeground(Color.RED);
		gridCenterPanel.add(lblError);

		JButton btnInserisci = new JButton("Inserisci");
		btnInserisci.setBackground(UIManager.getColor("Button.background"));
		btnInserisci.addActionListener(segreteriaListener);
		centerPanel.add(btnInserisci, BorderLayout.SOUTH);
	}

	/**
	 * metodo che resetta i campi di input
	 */
	public void clearAll() {
		lblError.setText("");
		fieldNome.setText("");
		fieldDescrizione.setText("");
		fieldMateriali.setText("");
	}

	public String getFieldNome() {return fieldNome.getText();}

	public String getFieldDescrizione() {return fieldDescrizione.getText();}

	public JComboBox<String> getCbSport() {return cbSport;}

	public String getFieldMateriali() {return fieldMateriali.getText();}

	public void setLblError(String msgError) {this.lblError.setText(msgError);}

}
