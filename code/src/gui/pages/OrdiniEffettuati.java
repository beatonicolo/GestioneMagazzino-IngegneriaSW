package gui.pages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import controller.GlobalListener;
import controller.NegozioListener;
import gui.resources.BasicPanel;
import model.dbcom.DataSource;
import model.entities.Negozio;
import model.entities.Ordine;

/**
 * Classe che definisce la schermata di visualizzazione degli ordini effettuati in precedenza dal negozio 
 * che ha eseguito il login
 * @author beato
 *
 */
public class OrdiniEffettuati extends BasicPanel {

	private JTextArea areaDettagli;
	private JList <String> listOrdiniEffettuati;
	private List<Ordine> listaOrdini;

	/**
	 * Costruttore classe schermata ordini precedenti
	 * @param negozioListener listener che gestisce eventi,e conseguenti operazioni, 
	 * generati dalle schermate dedicate ai negozi
	 */
	public OrdiniEffettuati(NegozioListener negozioListener,Negozio negozio) {
	
		super(negozioListener);
		
		JPanel panel = new JPanel();
		panel.setForeground(Color.WHITE);
		this.add(panel,BorderLayout.CENTER);
		GridLayout gl_panel = new GridLayout(1, 2);
		gl_panel.setVgap(2);
		gl_panel.setHgap(3);
		panel.setLayout(gl_panel);
		
		JLabel lblOrdiniEffettuati_1 = new JLabel("Ordini Effettuati");
		this.addToNavigationBar(lblOrdiniEffettuati_1);
		
		JButton btnNuovoOrdine = new JButton("Nuovo Ordine");
		btnNuovoOrdine.setForeground(Color.DARK_GRAY);
		btnNuovoOrdine.setBackground(Color.WHITE);
		btnNuovoOrdine.addActionListener((GlobalListener) negozioListener);
		this.addToNavigationBar(btnNuovoOrdine);
		

		//pannello di sinistra con lista ordini
		JPanel westPanel = new JPanel();
		westPanel.setBackground(Color.WHITE);
		westPanel.setLayout(new BorderLayout());
		JLabel lblOrdiniEffettuati = new JLabel("Ordini effettuati:");
		lblOrdiniEffettuati.setHorizontalAlignment(SwingConstants.CENTER);
		lblOrdiniEffettuati.setBackground(Color.DARK_GRAY);
		westPanel.add(lblOrdiniEffettuati,BorderLayout.NORTH);

		listaOrdini= DataSource.getInstance().getListaOrdiniNegozio(negozio.getCodFisc());
		DefaultListModel<String> listModel= new DefaultListModel <String> ();
		for (int i=0; i< listaOrdini.size();i++) {
			Ordine ordine=listaOrdini.get(i);
			listModel.addElement(ordine.getCodice());
		}
		listOrdiniEffettuati = new JList <String> (listModel);
		listOrdiniEffettuati.addListSelectionListener(negozioListener);

		JScrollPane scrollPaneWest = new JScrollPane(listOrdiniEffettuati);
		westPanel.add(scrollPaneWest);		

		//pannello di destra con dettagli ordine selezionato
		JPanel eastPanel = new JPanel();
		eastPanel.setBackground(Color.WHITE);
		eastPanel.setLayout(new BorderLayout());
		JLabel lblDettagliOrdine = new JLabel("Dettagli Ordine:");
		lblDettagliOrdine.setHorizontalAlignment(SwingConstants.CENTER);
		lblDettagliOrdine.setForeground(Color.DARK_GRAY);
		eastPanel.add(lblDettagliOrdine,BorderLayout.NORTH);
		areaDettagli = new JTextArea("");
		JScrollPane scrollPaneEast = new JScrollPane(areaDettagli);
		areaDettagli.setEditable(false);
		eastPanel.add(scrollPaneEast);

		panel.add(westPanel);
		panel.add(eastPanel);	
	}

	public void setTextArea(String text) {this.areaDettagli.setText(text);}

	public JList<String> getListOrdiniEffettuati() {return listOrdiniEffettuati;}

	public List<Ordine> getListaOrdini() {return listaOrdini;}

}
