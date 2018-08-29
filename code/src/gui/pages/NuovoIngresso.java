package gui.pages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import controller.GlobalListener;
import controller.MagazziniereListener;
import gui.resources.BasicPanel;
import model.dbcom.DataSource;

/**
 * Classe che definisce la schermata per l'inserimento di un ingresso
 * @author beato
 *
 */
public class NuovoIngresso extends BasicPanel {
	private JTextField fieldPosizione;
	private JTextField fieldDataProduzione;
	private JTextField fieldPrezzo;
	private JComboBox <String> cBoxTipoArticolo;
	private JTable table;

	/**
	 * Costruttore classe della schermata inserimento ingresso
	 * @param magazziniereListener listener che gestisce eventi,e conseguenti operazioni, 
	 * generati dalle schermate dedicate al personale del magazzino
	 */
	public NuovoIngresso(MagazziniereListener magazziniereListener) {

		super((GlobalListener)magazziniereListener);

		JButton btnSpostamento = new JButton("Spostamento");
		btnSpostamento.setForeground(Color.DARK_GRAY);
		btnSpostamento.setBackground(Color.WHITE);
		btnSpostamento.addActionListener((GlobalListener) magazziniereListener);
		this.addToNavigationBar(btnSpostamento);

		JLabel lblNuovoIngresso = new JLabel("Nuovo Ingresso");
		this.addToNavigationBar(lblNuovoIngresso);

		JButton btnNuovaUscita = new JButton("Nuova Uscita");
		btnNuovaUscita.setForeground(Color.DARK_GRAY);
		btnNuovaUscita.setBackground(Color.WHITE);
		btnNuovaUscita.addActionListener((GlobalListener) magazziniereListener);
		this.addToNavigationBar(btnNuovaUscita);

		JPanel centerPanel = new JPanel();
		centerPanel.setBackground(Color.WHITE);
		add(centerPanel, BorderLayout.CENTER);

		//creazione pannello principale
		JPanel gridCenterPanel = new JPanel();
		gridCenterPanel.setBackground(Color.WHITE);
		centerPanel.add(gridCenterPanel);
		gridCenterPanel.setLayout(new GridLayout(6, 2, 3, 2));

		JLabel lblNewLabel = new JLabel("Tipo Articolo:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gridCenterPanel.add(lblNewLabel);

		/* creazione combobox che elenca i tipi articolo presenti nel database
		 * e ne inserisce il nome nella combobox
		 */
		DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<String>();
		List <String> tipi= DataSource.getInstance().getStringListaTipi();
		for (int i=0;i<tipi.size();i++)
			cbModel.addElement(tipi.get(i));
		cBoxTipoArticolo = new JComboBox <String> (cbModel);
		cBoxTipoArticolo.setSelectedIndex(-1);
		cBoxTipoArticolo.addActionListener(magazziniereListener);
		gridCenterPanel.add(cBoxTipoArticolo);

		JLabel lblPosizione = new JLabel("Posizione:");
		lblPosizione.setHorizontalAlignment(SwingConstants.CENTER);
		gridCenterPanel.add(lblPosizione);

		fieldPosizione = new JTextField();
		gridCenterPanel.add(fieldPosizione);
		fieldPosizione.setColumns(10);

		JLabel lblDataProduzione = new JLabel("Data produzione:");
		lblDataProduzione.setHorizontalAlignment(SwingConstants.CENTER);
		gridCenterPanel.add(lblDataProduzione);

		fieldDataProduzione = new JTextField();
		gridCenterPanel.add(fieldDataProduzione);
		fieldDataProduzione.setColumns(10);

		JLabel lblPrezzo = new JLabel("Prezzo:");
		lblPrezzo.setHorizontalAlignment(SwingConstants.CENTER);
		gridCenterPanel.add(lblPrezzo);

		fieldPrezzo = new JTextField("0.00");
		gridCenterPanel.add(fieldPrezzo);
		fieldPrezzo.setEditable(false);

		JButton btnInserisci = new JButton("Inserisci");
		btnInserisci.addActionListener(magazziniereListener);
		gridCenterPanel.add(btnInserisci);

		JPanel eastPanel = new JPanel();
		eastPanel.setBackground(Color.WHITE);
		add(eastPanel, BorderLayout.EAST);
		eastPanel.setLayout(new BorderLayout(0, 0));

		JLabel lblRiepilogo = new JLabel("Riepilogo:");
		lblRiepilogo.setHorizontalAlignment(SwingConstants.CENTER);
		lblRiepilogo.setForeground(Color.DARK_GRAY);
		eastPanel.add(lblRiepilogo, BorderLayout.NORTH);

		//creazione tabella articoli in ingresso
		DefaultTableModel tableModel =new DefaultTableModel(){
			@Override
			public boolean isCellEditable(int row, int column)
			{return false;}//This causes all cells to be not editable
		};
		table= new JTable(tableModel);
		tableModel.addColumn("Tipo");
		tableModel.addColumn("Posizione");
		tableModel.addColumn("Data produzione");
		tableModel.addColumn("Prezzo");

		JScrollPane scrollTable = new JScrollPane(table);
		eastPanel.add(scrollTable, BorderLayout.CENTER);

		JPanel gridEastPanel = new JPanel();
		gridEastPanel.setBackground(Color.WHITE);
		eastPanel.add(gridEastPanel, BorderLayout.SOUTH);
		gridEastPanel.setLayout(new GridLayout(1, 2, 3, 2));

		JButton btnCancella = new JButton("Cancella");
		btnCancella.addActionListener(magazziniereListener);
		gridEastPanel.add(btnCancella);

		JButton btnConfermaIngresso = new JButton("Conferma Ingresso");
		btnConfermaIngresso.addActionListener(magazziniereListener);
		gridEastPanel.add(btnConfermaIngresso);
	}

	/**
	 * metodo che resetta la tabella degli articoli in ingresso
	 */
	public void clearTable() {
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

		int nRighe=table.getRowCount();
		for (int riga=0; riga<nRighe ;riga++) 
			tableModel.removeRow(0);
	}

	/**
	 * metodo che resetta i campi degli attributi dell'ingresso
	 */
	public void clearFields() {
		fieldPosizione.setText("");
		fieldDataProduzione.setText("");
		fieldPrezzo.setText("0.00");
	}

	public String getPosizione() {return fieldPosizione.getText();}

	public String getDataProduzione() {return fieldDataProduzione.getText();}

	public double getPrezzo() {
		double prezzo;
		try {prezzo=Double.valueOf(fieldPrezzo.getText());}
		catch (NumberFormatException e) {
			prezzo=0.00;
			fieldPrezzo.setText("0.00");
		}
		return prezzo;
	}
	public void setPrezzo(double prezzo) {this.fieldPrezzo.setText(String.format(Locale.ROOT,"%.2f", prezzo));}

	public JTextField getFieldPrezzo() {return fieldPrezzo;}

	public JComboBox<String> getcBoxTipoArticolo() {return cBoxTipoArticolo;}

	public JTable getTable() {return table;}
	public void setTable(JTable table) {this.table = table;}

}
