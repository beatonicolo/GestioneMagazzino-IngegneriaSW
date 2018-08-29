package gui.pages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.SystemColor;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import controller.GlobalListener;
import controller.SegreteriaListener;
import gui.resources.BasicPanel;
import model.dbcom.DataSource;
import model.entities.Ingresso;

/**
 * Classe che definisce la schermata di visualizzazione degli ingressi
 * @author beato
 *
 */
public class MovimentiInEntrata extends BasicPanel {

	private JList <String> listStringIngressi;
	private JTextArea dettagliIngresso;
	private List<Ingresso> listaIngressi;

	/**
	 * Costrutto classe della schermata di consultazione degli ingressi
	 * @param segreteriaListener listener che si occupa della gestione degli eventi, e le conseguenti
	 * azioni da eseguire, generati nelle schermate dedicate al personale della segreteria.
	 */
	public MovimentiInEntrata(SegreteriaListener segreteriaListener) {
		
		super((GlobalListener) segreteriaListener);

		JButton btnUscite = new JButton("Uscite");
		btnUscite.setForeground(Color.DARK_GRAY);
		btnUscite.setBackground(Color.WHITE);
		btnUscite.addActionListener((GlobalListener) segreteriaListener);
		this.addToNavigationBar(btnUscite);

		JLabel lblEntrate = new JLabel("Entrate");
		lblEntrate.setBackground(SystemColor.text);
		lblEntrate.setForeground(Color.BLACK);
		this.addToNavigationBar(lblEntrate);

		JButton btnInserimentoTipoArticolo = new JButton("Inserimento Tipo Articolo");
		btnInserimentoTipoArticolo.setForeground(Color.DARK_GRAY);
		btnInserimentoTipoArticolo.setBackground(Color.WHITE);
		btnInserimentoTipoArticolo.addActionListener((GlobalListener) segreteriaListener);
		this.addToNavigationBar(btnInserimentoTipoArticolo);

		//creazione pannello di sinistra, con lista ingressi incapsulata in uno scrollpanel	
		//creazione della lista degli ingressi
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		listaIngressi=DataSource.getInstance().listaIngressi();
		for (int i=0;i<listaIngressi.size();i++)
			listModel.addElement(listaIngressi.get(i).getCodice());
		listStringIngressi = new JList <String> (listModel);
		listStringIngressi.addListSelectionListener(segreteriaListener);
		listStringIngressi.setBackground(Color.WHITE);
		JScrollPane scrollWest = new JScrollPane(listStringIngressi);
		add(scrollWest, BorderLayout.WEST);

		//creazione area di dettaglio degli ingressi e incapsulamento in uno scroll panel
		dettagliIngresso= new JTextArea("Seleziona un ingresso dalla lista a sinistra");
		dettagliIngresso.setBackground(Color.WHITE);
		dettagliIngresso.setEditable(false);
		JScrollPane scrollCenter = new JScrollPane(dettagliIngresso);
		add(scrollCenter, BorderLayout.CENTER);
	}

	public JList<String> getJlistIngressi() {return listStringIngressi;}

	public void setDettagliIngresso(String dettagliIngresso) {this.dettagliIngresso.setText(dettagliIngresso);}

	public List<Ingresso> getListaIngressi() {return listaIngressi;}
}
