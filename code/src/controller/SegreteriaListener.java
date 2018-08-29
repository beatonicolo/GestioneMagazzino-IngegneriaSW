package controller;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import gui.pages.InserimentoTipo;
import gui.pages.MovimentiInEntrata;
import gui.pages.MovimentiInUscita;
import gui.resources.MyFrame;
import model.entities.Ingresso;
import model.entities.Ordine;
import model.entities.Uscita;

/**
 * Classe che implementa il lister che gestisce gli eventi generati dai componenti nelle schermate dedicate
 * ai dipendenti della segreteria
 * @author beato
 *
 */
public class SegreteriaListener extends GlobalListener implements ListSelectionListener {

	/**
	 * costruttore listener schermate segreteria
	 * @param frame
	 */
	public SegreteriaListener (MyFrame frame) {
		super(frame);
	}

	/**
	 * Implementazione del metodo dell'interfaccia ActionListener che identifica il componente che ha generato l'evento e chiama il metodo
	 * per svolgere le azioni richieste
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		JButton btn = (JButton) e.getSource();
		String caller = btn.getText();
		navigazionePerformed(caller);// controllo se il generante è per navigazione

		if (caller.equals("Filtra")) {
			doFiltra();
		}
		else if (caller.equals("Dettaglio")) {
			doDettaglio();
		}
		else if (caller.equals("Inserisci")) {
			doInserisci();
		}
	}
	/**
	 * implementazione del metodo dell'interfaccia ListSelectionListener che riceve la notifica di un cambio
	 * dell'elemento selezionato nella lista degli ingressi in magazzino
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			doVisualizzaDettagli();
		}
	}

	/**
	 * metodo incaricato di visualizzare i dettegli dell'ingresso selezionato
	 */
	private void doVisualizzaDettagli() {
		MovimentiInEntrata panel = (MovimentiInEntrata)frame.getContentPane();

		Ingresso ingresso= panel.getListaIngressi().get(panel.getJlistIngressi().getSelectedIndex());
		panel.setDettagliIngresso(ingresso.toString());
	}

	/**
	 * metodo che mostra la lista delle uscite filtrate per negozio
	 */
	private void doFiltra() {
		MovimentiInUscita panel = (MovimentiInUscita) frame.getContentPane();

		String negozio = panel.getFieldNegozio();
		List <Uscita> listaUscite;
		DefaultTableModel tableModel= (DefaultTableModel) panel.getTableUscite().getModel();

		//invia negozio e ottieni lista uscite per negozio scelto
		if (negozio.equals("Visualizza tutti"))
			listaUscite=ds.getListaUscite();
		else {
			//chiedo al database la lista uscite filtrata per negozio
			listaUscite=ds.getUscitePerNegozio(negozio);
			
		}
		panel.setListaUscite(listaUscite);
		//elimino righe presenti in tabella
		int nRighe=panel.getTableUscite().getRowCount();
		for(int i=0;i<nRighe;i++)
			tableModel.removeRow(0);

		//aggiungo righe richieste
		for (int i=0;i<listaUscite.size();i++) {
			Uscita uscita = listaUscite.get(i);
			tableModel.addRow(new Object[] {uscita.getData(),uscita.getBolla()});
		}
		panel.setTextDettaglio("");
		panel.setLblError("");
	}

	/**
	 * metodo incaricato di mostrare i dettagli dell'uscita selezionata e dell'ordine a cui 
	 * questa corrisponde
	 */
	private void doDettaglio() {
		MovimentiInUscita panel = (MovimentiInUscita) frame.getContentPane();

		//controllo che ci sia una riga selezionata
		if(panel.getTableUscite().getSelectedRow()>=0) {
			Uscita uscitaSel = panel.getListaUscite().get(panel.getTableUscite().getSelectedRow());

			Ordine ordineSel = ds.getOrdineDaUscita(uscitaSel.getBolla(), uscitaSel.getData());

			panel.setTextDettaglio(uscitaSel.toString() + ordineSel.toString());
		}
	}

	/**
	 * metodo che si occupa di inserire nel database un nuovo tipo articolo
	 */
	private void doInserisci() {
		InserimentoTipo panel = (InserimentoTipo) frame.getContentPane();

		String nome = panel.getFieldNome(),
				descrizione=panel.getFieldDescrizione(),
				sport = panel.getCbSport().getSelectedItem().toString(),
				materiali = panel.getFieldMateriali(); 

		if(nome.equals("")||descrizione.equals("")||materiali.equals("")) 
			panel.setLblError("Completare tutti i campi!");
		else if (ds.verificaNomeTipo(nome))
			panel.setLblError("Nome tipo gia' presente nel sistema!");
		else {
			ds.inserimentoTipoArticolo(nome, descrizione, materiali, sport);
			JOptionPane.showMessageDialog(null,"Tipo Articolo inserito con successo!",
					"TIPO INSERITO",3);
			panel.clearAll();
		}
	}

}

