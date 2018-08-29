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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import controller.GlobalListener;
import controller.SegreteriaListener;
import gui.resources.BasicPanel;
import model.dbcom.DataSource;
import model.entities.Uscita;

/**
 * Classe che definisce la schermata di consultazione delle uscite
 * @author beato
 *
 */
public class MovimentiInUscita extends BasicPanel {

	private JComboBox <String> cbNegozio;
	private JTable tableUscite;
	private JTextArea textDettaglio;
	private JLabel lblError;
	private List <Uscita> listaUscite;

	/**
	 * costruttore della classe di consultazione delle uscite
	 * @param segreteriaListener listener che si occupa della gestione degli eventi, e le conseguenti
	 * azioni da eseguire, generati nelle schermate dedicate al personale della segreteria.
	 */
	public MovimentiInUscita(SegreteriaListener segreteriaListener) {
		
		super((GlobalListener) segreteriaListener);

		DataSource ds = DataSource.getInstance();

		JLabel lblUscite = new JLabel("Uscite");
		lblUscite.setForeground(Color.BLACK);
		this.addToNavigationBar(lblUscite);

		JButton btnEntrate = new JButton("Entrate");
		btnEntrate.setBackground(Color.WHITE);
		btnEntrate.setForeground(Color.DARK_GRAY);
		btnEntrate.addActionListener((GlobalListener) segreteriaListener);
		this.addToNavigationBar(btnEntrate);
		
		JButton btnInserimentoTipoArticolo = new JButton("Inserimento Tipo Articolo");
		btnInserimentoTipoArticolo.setForeground(Color.DARK_GRAY);
		btnInserimentoTipoArticolo.setBackground(Color.WHITE);
		btnInserimentoTipoArticolo.addActionListener((GlobalListener) segreteriaListener);
		this.addToNavigationBar(btnInserimentoTipoArticolo);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		//crezione del pannello superiore contenente i componenti per filtrare i risultati
		JPanel gridNorthPanel = new JPanel();
		gridNorthPanel.setBackground(Color.WHITE);
		panel.add(gridNorthPanel, BorderLayout.NORTH);
		gridNorthPanel.setLayout(new GridLayout(2, 2, 3, 2));

		JLabel lblNegozio = new JLabel("Negozio:");
		lblNegozio.setHorizontalAlignment(SwingConstants.CENTER);
		gridNorthPanel.add(lblNegozio);
		
		DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<String>();
		List <String> negozi= ds.getListaCFNegozi();
		cbModel.addElement("Visualizza tutti");
		for (int i=0;i<negozi.size();i++)
			cbModel.addElement(negozi.get(i));
		cbNegozio = new JComboBox <String>(cbModel);
		cbNegozio.setSelectedIndex(0);
		gridNorthPanel.add(cbNegozio);

		JButton btnFiltra = new JButton("Filtra");
		btnFiltra.addActionListener(segreteriaListener);
		gridNorthPanel.add(btnFiltra);

		lblError = new JLabel("");
		lblError.setHorizontalAlignment(SwingConstants.CENTER);
		lblError.setForeground(Color.RED);
		gridNorthPanel.add(lblError);

		//creazione pannello principale contenente lista delle uscite e area di dettaglio
		JPanel centerPanel = new JPanel();
		centerPanel.setBackground(Color.WHITE);
		panel.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));

		//creazione tabella con elenco di tutte le uscite		
		DefaultTableModel tableModel = new DefaultTableModel(){
			@Override
			public boolean isCellEditable(int row, int column)
			{return false;}
		};
		listaUscite= ds.getListaUscite();
		tableModel.addColumn("Data");
		tableModel.addColumn("N. bolla");
		for(int i=0; i<listaUscite.size();i++) {
			Uscita uscita = listaUscite.get(i);
			tableModel.addRow(new Object[] {uscita.getData(),uscita.getBolla()});
		}
		tableUscite = new JTable(tableModel);

		JScrollPane spListU = new JScrollPane(tableUscite);
		centerPanel.add(spListU, BorderLayout.CENTER);

		textDettaglio = new JTextArea("Dettagli Uscita");
		textDettaglio.setColumns(30);
		JScrollPane spDettaglio = new JScrollPane(textDettaglio);
		centerPanel.add(spDettaglio, BorderLayout.EAST);

		JButton btnDettaglio = new JButton("Dettaglio");
		btnDettaglio.addActionListener(segreteriaListener);
		centerPanel.add(btnDettaglio, BorderLayout.SOUTH);

		JLabel lblNewLabel = new JLabel("Movimenti In Uscita");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		centerPanel.add(lblNewLabel, BorderLayout.NORTH);

	}

	public String getFieldNegozio() {return cbNegozio.getSelectedItem().toString();}

	public JTable getTableUscite() {return tableUscite;}

	public void setTextDettaglio(String textDettaglio) {this.textDettaglio.setText(textDettaglio);}

	public void setLblError(String errorMsg) {this.lblError.setText(errorMsg);}

	public List<Uscita> getListaUscite() {return listaUscite;}

	public void setListaUscite(List<Uscita> listaUscite) {this.listaUscite = listaUscite;}


}
