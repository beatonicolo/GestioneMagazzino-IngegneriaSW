package gui.pages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import controller.GlobalListener;
import controller.MagazziniereListener;
import gui.resources.BasicPanel;
import model.dbcom.DataSource;
import model.entities.Articolo;
import model.entities.Ordine;

/**
 * Classe che definisce la schermata di inserimento di un'uscita
 * @author beato
 *
 */
public class NuovaUscita extends BasicPanel {
	private JTable tableArtInMag;
	private JTable tableArtUsc;
	private JComboBox <String> cbOrdine;
	private JComboBox <String> cbSpedizioniere;
	private List<Ordine> ordiniEvadibili;
	private JTextArea areaDettagliOrdine;
	private Ordine ordine;
	private List<Articolo> articoliInMagazzino;
	private List<Articolo> articoliOut;
	private DataSource ds;

	/**
	 * Costruttore classe schermata di inserimento uscita
	 * @param magazziniereListener listener che gestisce eventi,e conseguenti operazioni, 
	 * generati dalle schermate dedicate al personale del magazzino
	 */
	public NuovaUscita (MagazziniereListener magazziniereListener) {
		
		super((GlobalListener)magazziniereListener);
		this.ds = DataSource.getInstance();
		
		articoliOut =new ArrayList<Articolo>();

		JButton btnSpostamento = new JButton("Spostamento");
		btnSpostamento.addActionListener((GlobalListener) magazziniereListener);
		btnSpostamento.setBackground(Color.WHITE);
		btnSpostamento.setForeground(Color.DARK_GRAY);
		this.addToNavigationBar(btnSpostamento);

		JButton btnNuovoIngresso = new JButton("Nuovo Ingresso");
		btnNuovoIngresso.addActionListener((GlobalListener) magazziniereListener);
		btnNuovoIngresso.setForeground(Color.DARK_GRAY);
		btnNuovoIngresso.setBackground(Color.WHITE);
		this.addToNavigationBar(btnNuovoIngresso);

		JLabel lblNuovaUscita = new JLabel("Nuova Uscita");
		lblNuovaUscita.setBackground(Color.LIGHT_GRAY);
		this.addToNavigationBar(lblNuovaUscita);

		JPanel gridCenterPanel = new JPanel();
		gridCenterPanel.setBackground(Color.WHITE);
		add(gridCenterPanel, BorderLayout.CENTER);
		gridCenterPanel.setLayout(new GridLayout(1, 4, 3, 0));

		/*creazione prima colonna di sinistra contenente gli attributi dell'uscita
		 * 
		 */
		JPanel primoPanel = new JPanel();
		primoPanel.setBackground(Color.WHITE);
		gridCenterPanel.add(primoPanel);
		primoPanel.setLayout(new BorderLayout(0, 0));

		JPanel gridPrimoPanel = new JPanel();
		gridPrimoPanel.setBackground(Color.WHITE);
		primoPanel.add(gridPrimoPanel, BorderLayout.NORTH);
		gridPrimoPanel.setLayout(new GridLayout(5, 1, 0, 0));

		JLabel lblRiferimentoOrdine = new JLabel("Riferimento Ordine:");
		lblRiferimentoOrdine.setHorizontalAlignment(SwingConstants.CENTER);
		gridPrimoPanel.add(lblRiferimentoOrdine);

		/*
		 *creazione combobox contente il numero degli ordini effettuati ma che non sono stati ancora evasi,
		 *il tutto letto dal db
		 */
		DefaultComboBoxModel<String> cbModelOrdini = new DefaultComboBoxModel<String>();
		ordiniEvadibili= ds.getListaOrdiniInevasi();
		for (int i=0;i<ordiniEvadibili.size();i++)
			cbModelOrdini.addElement(ordiniEvadibili.get(i).getCodice());
		cbOrdine = new JComboBox <String> (cbModelOrdini);
		if (!ordiniEvadibili.isEmpty())
			cbOrdine.setSelectedIndex(0);
		gridPrimoPanel.add(cbOrdine);

		JButton btnCaricaOrdine = new JButton("Carica Ordine");
		btnCaricaOrdine.addActionListener(magazziniereListener);
		gridPrimoPanel.add(btnCaricaOrdine);

		JLabel lblSpedizioniere = new JLabel("Spedizioniere:");
		lblSpedizioniere.setHorizontalAlignment(SwingConstants.CENTER);
		gridPrimoPanel.add(lblSpedizioniere);

		//creazione combobox con lista spedizionieri presenti nel db
		DefaultComboBoxModel<String> cbModelSpedizionieri = new DefaultComboBoxModel<String>();
		List <String> spedizionieri= ds.getListaSpedizionieri();
		for (int i=0;i<spedizionieri.size();i++)
			cbModelSpedizionieri.addElement(spedizionieri.get(i));
		cbSpedizioniere = new JComboBox<String>(cbModelSpedizionieri);
		cbSpedizioniere.setSelectedIndex(0);
		gridPrimoPanel.add(cbSpedizioniere);

		//creazione seconda colonna contente i dettagli dell'ordine che si è scelto di evadere
		JPanel secondoPanel = new JPanel();
		secondoPanel.setBackground(Color.WHITE);
		gridCenterPanel.add(secondoPanel);
		secondoPanel.setLayout(new BorderLayout(0, 0));

		JLabel lblDettagliOrdine = new JLabel("Dettagli Ordine:");
		lblDettagliOrdine.setHorizontalAlignment(SwingConstants.CENTER);
		secondoPanel.add(lblDettagliOrdine, BorderLayout.NORTH);

		areaDettagliOrdine = new JTextArea();
		areaDettagliOrdine.setEditable(false);
		secondoPanel.add(areaDettagliOrdine, BorderLayout.SOUTH);		
		JScrollPane scrollPane = new JScrollPane(areaDettagliOrdine);
		secondoPanel.add(scrollPane, BorderLayout.CENTER);

		/*creazione terza colonna contenente la lista degli articoli presenti in magazzino
		 * che rispettano il tipo ordinato
		 * 
		 */
		JPanel terzoPanel = new JPanel();
		terzoPanel.setBackground(Color.WHITE);
		gridCenterPanel.add(terzoPanel);
		terzoPanel.setLayout(new BorderLayout(0, 0));

		JLabel lblArticoliInMagazzino = new JLabel("Articoli in Magazzino:");
		lblArticoliInMagazzino.setHorizontalAlignment(SwingConstants.CENTER);
		terzoPanel.add(lblArticoliInMagazzino, BorderLayout.NORTH);

		DefaultTableModel magazzinoModel = new DefaultTableModel(){
			@Override
			public boolean isCellEditable(int row, int column)
			{return false;}//rende le celle della tabella non editabili
		};
		tableArtInMag = new JTable(magazzinoModel);
		magazzinoModel.addColumn("Codice");
		magazzinoModel.addColumn("Tipo");
		JScrollPane sTabMag = new JScrollPane(tableArtInMag);
		terzoPanel.add(sTabMag, BorderLayout.CENTER);

		JButton btnAggiungi = new JButton("Aggiungi");
		btnAggiungi.addActionListener(magazziniereListener);
		terzoPanel.add(btnAggiungi, BorderLayout.SOUTH);

		/*
		 * creazione quarta colonna che comprende la lista (tabella) degli articoli scelti per l'uscita
		 */
		JPanel quartoPanel = new JPanel();
		quartoPanel.setBackground(Color.WHITE);
		gridCenterPanel.add(quartoPanel);
		quartoPanel.setLayout(new BorderLayout(0, 0));

		JLabel lblArticoliInUscita = new JLabel("Articoli in Uscita:");
		lblArticoliInUscita.setHorizontalAlignment(SwingConstants.CENTER);
		quartoPanel.add(lblArticoliInUscita, BorderLayout.NORTH);

		DefaultTableModel uscitaModel = new DefaultTableModel(){
			@Override
			public boolean isCellEditable(int row, int column)
			{return false;}//rende le celle della tabella non editabili
		};
		tableArtUsc = new JTable(uscitaModel);
		uscitaModel.addColumn("Codice");
		uscitaModel.addColumn("Tipo");
		JScrollPane sTabUsc = new JScrollPane(tableArtUsc);
		quartoPanel.add(sTabUsc);

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		quartoPanel.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(1, 2, 0, 0));

		JButton btnElimina = new JButton("Elimina");
		btnElimina.addActionListener(magazziniereListener);
		panel.add(btnElimina);

		JButton btnConfermaUscita = new JButton("Conferma Uscita");
		btnConfermaUscita.addActionListener(magazziniereListener);
		panel.add(btnConfermaUscita);
	}

	/**
	 * Metodo che re-inizializza i campi della schermata.
	 * Ricarica la alista degli ordini evadibili, chiama il metodo che svuota le due tabelle, 
	 * pulisce l'area di dettaglio dell'ordine.
	 */
	public void clearAll() {
		DefaultComboBoxModel<String> cbModelOrdini = new DefaultComboBoxModel<String>();
		ordiniEvadibili= ds.getListaOrdiniInevasi();
		for (int i=0;i<ordiniEvadibili.size();i++)
			cbModelOrdini.addElement(ordiniEvadibili.get(i).getCodice());
		cbOrdine.setModel(cbModelOrdini);
		if (!ordiniEvadibili.isEmpty())
			cbOrdine.setSelectedIndex(0);
		areaDettagliOrdine.setText("");
		clearTables();
	}

	/**
	 * Metodo che svuota le tabelle
	 */
	public void clearTables() {
		DefaultTableModel magazzinoModel = (DefaultTableModel) tableArtInMag.getModel();
		DefaultTableModel usciteModel = (DefaultTableModel) tableArtUsc.getModel();

		int nRighe=tableArtInMag.getRowCount();
		for (int riga=0;riga<nRighe;riga++)
			magazzinoModel.removeRow(0);

		nRighe=tableArtUsc.getRowCount();
		for (int riga=0;riga<nRighe;riga++) {
			//elimino anche gli elementi della lista articoliOut
			articoliOut.remove(0);
			usciteModel.removeRow(0);}
	}

	public JTable getTableArtInMag() {return tableArtInMag;}

	public JTable getTableArtUsc() {return tableArtUsc;}

	public JComboBox<String> getCbOrdine() {return cbOrdine;}

	public JComboBox<String> getCbSpedizioniere() {return cbSpedizioniere;}

	public List<Ordine> getOrdiniEvadibili() {return ordiniEvadibili;}

	public void setAreaDettagliOrdine(String dettagli) {this.areaDettagliOrdine.setText(dettagli);}

	public Ordine getOrdine() {return ordine;}
	public void setOrdine(Ordine ordine) {this.ordine = ordine;}

	public List<Articolo> getArticoliInMagazzino() {return articoliInMagazzino;}
	public void setArticoliInMagazzino(List<Articolo> articoliInMagazzino) {this.articoliInMagazzino = articoliInMagazzino;}

	public void addToArticoliOut(String codice){
		for(int i=0;i<articoliInMagazzino.size();i++) 
			if (articoliInMagazzino.get(i).getCodice().equals(codice))
				articoliOut.add(articoliInMagazzino.get(i));
		}
	
	public void removeFromArticoliOut(int index){articoliOut.remove(index);}
	
	public List<Articolo> getArticoliOut(){return articoliOut;}

}
