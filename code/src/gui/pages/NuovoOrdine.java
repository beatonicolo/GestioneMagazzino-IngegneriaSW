package gui.pages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import controller.GlobalListener;
import controller.NegozioListener;
import gui.resources.BasicPanel;
import model.dbcom.DataSource;
import model.entities.TipoArticolo;
import model.entities.TipoArticoloOr;

/**
 * Classe che definisce la schermata di inserimento di un nuovo ordine
 * @author beato
 *
 */
public class NuovoOrdine extends BasicPanel {

	private JTextField fieldQuantita;
	private JList <String> list;
	private JTable carrello;
	private JTextField fieldPrice;
	private List <TipoArticolo> listaTipi;
	private List<TipoArticoloOr> listaArticoliOrdinati;
	private double prezzoTot=0.00;

	/**
	 * Costruttore classe schermata nuovo ordine
	 * @param negozioListener listener che gestisce eventi,e conseguenti operazioni, 
	 * generati dalle schermate dedicate ai negozi
	 */
	public NuovoOrdine(NegozioListener negozioListener) {
		
		super(negozioListener);
		
		JButton btnOrdiniEffettuati = new JButton("Ordini Effettuati");
		btnOrdiniEffettuati.setForeground(Color.DARK_GRAY);
		btnOrdiniEffettuati.setBackground(Color.WHITE);
		btnOrdiniEffettuati.addActionListener((GlobalListener)negozioListener);
		this.addToNavigationBar(btnOrdiniEffettuati);

		JLabel lblNuovoOrdine = new JLabel("Nuovo Ordine");
		this.addToNavigationBar(lblNuovoOrdine);

		JPanel container = new JPanel();
		container.setBackground(Color.WHITE);
		add(container,BorderLayout.CENTER);
		container.setLayout(new GridLayout(1, 2));

		//creazione pannello sinistro contenente lista dei tipi articolo, e la quantita
		JPanel westPanel = new JPanel();
		westPanel.setBackground(Color.WHITE);
		container.add(westPanel);
		westPanel.setLayout(new BorderLayout());

		JLabel lblProdotti = new JLabel("Prodotti:");
		lblProdotti.setHorizontalAlignment(SwingConstants.CENTER);
		lblProdotti.setForeground(Color.DARK_GRAY);
		westPanel.add(lblProdotti,BorderLayout.NORTH);

		//creo modello della jlist e lo popolo con il campo nome di tutti i tipi articolo
		DefaultListModel<String> listaModel=new DefaultListModel<String> ();
		listaTipi = DataSource.getInstance().getListaTipi();
		for(int i =0;i<listaTipi.size();i++)
			listaModel.addElement(listaTipi.get(i).getNome());		
		list = new JList<String> (listaModel);
		list.setSelectedIndex(0);

		//creazione pannello di destra con lista tipi articolo, quantià, prezzo
		listaArticoliOrdinati=new ArrayList<TipoArticoloOr>();
		DefaultTableModel carrelloModel =new DefaultTableModel(){
			@Override
			public boolean isCellEditable(int row, int column)
			{return false;}//This causes all cells to be not editable
		};
		carrello= new JTable(carrelloModel);
		carrelloModel.addColumn("Prodotto:");
		carrelloModel.addColumn("Quantit\u00E0:");
		carrelloModel.addColumn("Importo:");

		JScrollPane scrollPaneWest = new JScrollPane(list);
		westPanel.add(scrollPaneWest,BorderLayout.CENTER);

		JPanel southWestPanel = new JPanel();
		southWestPanel.setBackground(Color.WHITE);
		westPanel.add(southWestPanel,BorderLayout.SOUTH);
		southWestPanel.setLayout(new GridLayout(1, 3, 3, 2));

		JLabel lblQuantita = new JLabel("Quantit\u00E0:");
		lblQuantita.setHorizontalAlignment(SwingConstants.CENTER);
		lblQuantita.setForeground(Color.DARK_GRAY);
		southWestPanel.add(lblQuantita);

		fieldQuantita = new JTextField("1");
		southWestPanel.add(fieldQuantita);

		JButton btnAggiungi = new JButton("Aggiungi");
		btnAggiungi.addActionListener(negozioListener);
		southWestPanel.add(btnAggiungi);

		JPanel eastPanel = new JPanel();
		eastPanel.setBackground(Color.WHITE);
		container.add(eastPanel);
		eastPanel.setLayout(new BorderLayout());

		JLabel lblProdottiOrdinati = new JLabel("Prodotti ordinati:");
		lblProdottiOrdinati.setHorizontalAlignment(SwingConstants.CENTER);
		lblProdottiOrdinati.setForeground(Color.DARK_GRAY);
		eastPanel.add(lblProdottiOrdinati,BorderLayout.NORTH);

		JScrollPane scrollPanelEast = new JScrollPane(carrello);
		eastPanel.add(scrollPanelEast,BorderLayout.CENTER);

		JPanel southEastPanel = new JPanel();
		southEastPanel.setBackground(Color.WHITE);
		eastPanel.add(southEastPanel,BorderLayout.SOUTH);
		southEastPanel.setLayout(new GridLayout(1, 3, 3, 2));

		JButton btnConferma = new JButton("Conferma");
		btnConferma.addActionListener(negozioListener);
		southEastPanel.add(btnConferma);

		JButton btnElimina = new JButton("Elimina");
		btnElimina.addActionListener(negozioListener);
		southEastPanel.add(btnElimina);

		fieldPrice = new JTextField("0.00");
		fieldPrice.setForeground(Color.RED);
		fieldPrice.setEditable(false);
		southEastPanel.add(fieldPrice);
	}

	/**
	 * Metodo che resetta tutti i campi e le tabelle della schermata
	 */
	public void clearAll() {
		DefaultTableModel carrelloModel = (DefaultTableModel) carrello.getModel();

		int nRighe=carrello.getRowCount();
		for (int riga=0; riga<nRighe ;riga++) {
			carrelloModel.removeRow(0);
			listaArticoliOrdinati.remove(0);
		}
		prezzoTot=0.00;
		fieldQuantita.setText("1");
		fieldPrice.setText("0.00");
	}


	public String getFieldQuantita() {return fieldQuantita.getText();}
	
	public JList<String> getList() {return list;}

	public JTable getCarrello() {return carrello;}

	public List<TipoArticolo> getListaTipi() {return listaTipi;}

	public List<TipoArticoloOr> getListaArticoliOrdinati() {return listaArticoliOrdinati;}

	public void setPrice(double prezzo) {this.fieldPrice.setText(String.format(Locale.ROOT,"%.2f", prezzo));}
	public double getPrezzoTot() {return prezzoTot;}
	public void setPrezzoTot(double prezzoTot) {this.prezzoTot = prezzoTot;}

}
