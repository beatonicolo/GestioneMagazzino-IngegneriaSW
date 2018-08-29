package controller;

import java.awt.event.ActionEvent;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import gui.pages.NuovaUscita;
import gui.pages.NuovoIngresso;
import gui.pages.Spostamento;
import gui.resources.MyFrame;
import model.entities.Articolo;
import model.entities.Ingresso;
import model.entities.Ordine;
import model.entities.TipoArticoloOr;
import model.entities.Uscita;

/**
 * Classe che implementa il listener che gestisce gli eventi generati dai componenti inseriti
 * nelle schermate dedicate ai magazzinieri
 * @author beato
 *
 */
public class MagazziniereListener extends GlobalListener {

	/**
	 * costruttore listener per schermate dedicate ai magazzinieri
	 * @param frame frame principale, utilizzato per accedere ai componenti delle schermate
	 */
	public MagazziniereListener(MyFrame frame) {
		super(frame);
	}

	/**
	 * implementazione del metodo che identifica il componente che ha generato l'evento e chiama il metodo
	 * che svolge le azioni richieste 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() instanceof JButton) {// controllo che eventi siano generati da buttons
			
			JButton btn = (JButton) e.getSource();
			String caller = btn.getText();
			navigazionePerformed(caller);// controllo se il generante è per navigazione
			
			if (caller.equals("Sposta"))
				doSposta();
			
			else if (caller.equals("Inserisci"))
				doInserisci();
			
			else if (caller.equals("Cancella"))
				doCancella();

			else if (caller.equals("Conferma Ingresso"))
				doConfermaIngresso();
			
			else if (caller.equals("Carica Ordine"))
				doCaricaOrdine();

			else if (caller.equals("Aggiungi"))
				doAggiungi();
			
			else if (caller.equals("Elimina"))
				doElimina();
			
			else if (caller.equals("Conferma Uscita")) 
				doConfermaUscita();
		}
		else { 	/*evento generato dalla selezione di tipoArticolo nella combobox presente
				*presente nella schermata NuovoIngresso
				*/
			NuovoIngresso panel = (NuovoIngresso) frame.getContentPane();
			if (panel.getcBoxTipoArticolo().getSelectedIndex()>=0)
				//controllo che ci sia almeno un tipo selezionato
				gestioneFieldPrezzo();
		}
	}

	/**
	 * Metodo che cambia la posizione di un articolo in magazzino
	 */
	private void doSposta() {
		Spostamento panel = (Spostamento) frame.getContentPane();
		String codArt, nuovaPosizione;
			codArt=panel.getCodArticolo();
			nuovaPosizione=panel.getNuovaPosizione();
		//controllo che il codice inserito corrisponda ad un articolo presente in magazzino
		boolean esiste=ds.verificaArticolo(codArt);
		
		if (codArt.equals("")||nuovaPosizione.equals(""))
			panel.setLblError("Compilare i campi codice e posizione");
		else if (esiste) {
			ds.modPosizione(nuovaPosizione, codArt);
			JOptionPane.showMessageDialog(null,"Spostamento articolo registrato con successo!",
					"ARTICOLO SPOSTATO",3);
			panel.clearAll();
		}
		else {
			panel.setLblError("codice articolo errato");
		}
	}
	/**
	 * Metodo carica i dati dell'ordine selezionato nella schermata NuovaUscita
	 */
	private void doCaricaOrdine() {
		NuovaUscita panel = (NuovaUscita) frame.getContentPane();

		DefaultTableModel magazzinoModel = (DefaultTableModel) panel.getTableArtInMag().getModel();
		List<Articolo> articoliMagazzino;

		if (!panel.getOrdiniEvadibili().isEmpty()) {
			Ordine ordine = panel.getOrdiniEvadibili().get(panel.getCbOrdine().getSelectedIndex());
			panel.setOrdine(ordine);

			panel.setAreaDettagliOrdine(ordine.toString());
			articoliMagazzino=ds.getArticoliUscenti(ordine.getCodice());
			panel.setArticoliInMagazzino(articoliMagazzino);

			panel.clearTables();

			for(int i=0;i<articoliMagazzino.size();i++)
				magazzinoModel.addRow(new Object[] {articoliMagazzino.get(i).getCodice(),articoliMagazzino.get(i).getTipoArticoloRef()});

			if(!(ds.verificaGiacenzePerOrdine(ordine.getCodice())))
				JOptionPane.showMessageDialog(null,"In magazzino non ci sono abbastanza articoli per evadere l'ordine!",
						"DISPONIBILITA' INSUFFICIENTE!",2);
		}
	}

	/**
	 * Medoto che aggiunge alla tabella degli articoli in uscita l'articolo selezionato della tabella 
	 * contenete gli articoli presenti in magazzino, e lo rimuove da quest'ultima
	 */
	private void doAggiungi() {
		NuovaUscita panel = (NuovaUscita) frame.getContentPane();

		DefaultTableModel magazzinoModel = (DefaultTableModel) panel.getTableArtInMag().getModel();
		DefaultTableModel usciteModel = (DefaultTableModel) panel.getTableArtUsc().getModel();

		if(panel.getTableArtInMag().getSelectedRow()>=0) {
			usciteModel.addRow(new Object[] {magazzinoModel.getValueAt(panel.getTableArtInMag().getSelectedRow(),0),magazzinoModel.getValueAt(panel.getTableArtInMag().getSelectedRow(),1)});
			panel.addToArticoliOut(magazzinoModel.getValueAt(panel.getTableArtInMag().getSelectedRow(),0).toString());
			magazzinoModel.removeRow(panel.getTableArtInMag().getSelectedRow());
		}
	}

	/**
	 * Metodo che elimina un articolo dalla tabella degli articoli in unscita lo riaggiunge alla tabella
	 * degli articoli presenti in magazzino
	 */
	private void doElimina() {
		NuovaUscita panel = (NuovaUscita) frame.getContentPane();

		DefaultTableModel magazzinoModel = (DefaultTableModel) panel.getTableArtInMag().getModel();
		DefaultTableModel usciteModel = (DefaultTableModel) panel.getTableArtUsc().getModel();

		if(panel.getTableArtUsc().getSelectedRow()>=0) {
			magazzinoModel.addRow(new Object[] {usciteModel.getValueAt(panel.getTableArtUsc().getSelectedRow(),0),usciteModel.getValueAt(panel.getTableArtUsc().getSelectedRow(),1)});
			panel.removeFromArticoliOut(panel.getTableArtUsc().getSelectedRow());
			usciteModel.removeRow(panel.getTableArtUsc().getSelectedRow());
		}
	}

	/**
	 * Metodo che registra un'uscita con gli articoli della tabella articoli in uscita
	 */
	private void doConfermaUscita() {
		NuovaUscita panel = (NuovaUscita) frame.getContentPane();
		
		DefaultTableModel usciteModel = (DefaultTableModel) panel.getTableArtUsc().getModel();
		List<Articolo> articoliUscenti = panel.getArticoliOut();

		if(usciteModel.getRowCount()>0) {
			Ordine ordineRif= panel.getOrdine();
			Uscita nuovaUscita= new Uscita (ds.nextUscita(),new Date(Calendar.getInstance().getTimeInMillis()),panel.getCbSpedizioniere().getSelectedItem().toString(),ordineRif.getCodice(),ordineRif.getNegozio(),articoliUscenti);
			if (checkUscita(articoliUscenti)) {
				ds.inserimentoUscita(nuovaUscita);
				JOptionPane.showMessageDialog(null,"Uscita registrata!",
						"USCITA REGISTRATA",3);
				panel.clearAll();
			}
		}
		else
			JOptionPane.showMessageDialog(null,"Nessun articolo inserito!",
					"ATTENZIONE!",2);
	}

	/**
	 * Metodo che controlla che gli articoli nella tabella in uscita soddisfino completamente gli articoli
	 * presenti nell'ordine caricato
	 * @param articoliUscenti lista degli articoli inseriti nella tabella articoli in uscita
	 * @return true se corrisponde all'ordine, false altrimenti
	 */
	private boolean checkUscita(List<Articolo> articoliUscenti) {
		NuovaUscita panel = (NuovaUscita) frame.getContentPane();
		
		List <TipoArticoloOr> articoliOrdinati=panel.getOrdine().getListaArticoliOrdinati();
		for(int nTipo=0;nTipo<articoliOrdinati.size();nTipo++) {
			int conteggio=0;
			for (int nArticolo=0;nArticolo<articoliUscenti.size();nArticolo++) {
				if (articoliUscenti.get(nArticolo).getTipoArticoloRef().equals(articoliOrdinati.get(nTipo).getNome()))
					conteggio++;
			}
			if (!(conteggio==articoliOrdinati.get(nTipo).getQuantita())) {
				JOptionPane.showMessageDialog(null,"Gli articoli non corrispondono ai tipi ordinati!",
						"USCITA ERRATA!",2);
				return false;
			}
		}
		return true;
	}

	/**
	 * Metodo che inserisce nella tabella degli articoli in ingresso un articolo
	 */
	private void doInserisci() {
		NuovoIngresso panel = (NuovoIngresso) frame.getContentPane();

		DefaultTableModel tableModel = (DefaultTableModel) panel.getTable().getModel();
		String tipoArticolo,
			posizione = panel.getPosizione(),
			dataProduzione = panel.getDataProduzione();
		double prezzo=panel.getPrezzo();

		if(posizione.equals("")|| dataProduzione.equals("")||prezzo<=0) 
			JOptionPane.showMessageDialog(null,"Attenzione completare i tutti i campi correttamente prima dell'inserimento",
					"ATTENZIONE!",2);
		else {
			tipoArticolo = panel.getcBoxTipoArticolo().getSelectedItem().toString();

			tableModel.addRow(new Object[] {tipoArticolo,posizione,dataProduzione,prezzo});

			panel.clearFields();
			gestioneFieldPrezzo();
		}
	}

	/**
	 * metodo che elimina un articolo dalla tabella degli articoli in ingresso
	 */
	private void doCancella() {
		NuovoIngresso panel = (NuovoIngresso) frame.getContentPane();

		if(panel.getTable().getSelectedRow()>=0) {
			DefaultTableModel tableModel = (DefaultTableModel) panel.getTable().getModel();
			tableModel.removeRow(panel.getTable().getSelectedRow());
		}
	}

	/**
	 * metodo che registra un ingresso con tutta la lista degli articoli entranti
	 */
	private void doConfermaIngresso() {
		NuovoIngresso panel = (NuovoIngresso) frame.getContentPane();

		String tipoArticolo,posizione,dataProduzione;
		double prezzo;
		DefaultTableModel tableModel = (DefaultTableModel) panel.getTable().getModel();
		List <Articolo> lista=new ArrayList <Articolo>();

		if(panel.getTable().getRowCount()>0) {
			String codIngresso=ds.nextIngresso();
			//itero sulle righe per leggere prodotti
			for (int riga=0; riga< panel.getTable().getRowCount();riga++) {			
				tipoArticolo = tableModel.getValueAt(riga, 0).toString();
				posizione = tableModel.getValueAt(riga, 1).toString();
				dataProduzione= tableModel.getValueAt(riga, 2).toString();
				prezzo= Double.parseDouble(tableModel.getValueAt(riga, 3).toString()); 	
				lista.add(new Articolo(prezzo,dataProduzione,posizione,tipoArticolo,codIngresso));
			}

			//inviarla per salvataggio in database
			Ingresso ingresso = new Ingresso(codIngresso,new Date(Calendar.getInstance().getTimeInMillis()),lista);
			ds.inserimentoIngresso(ingresso);
			JOptionPane.showMessageDialog(null,"Nuovo ingresso registrato con successo",
					"INGRESSO REGISTRATO",3);
			panel.clearTable();
			gestioneFieldPrezzo();
		}
		else
			JOptionPane.showMessageDialog(null,"Nessun articolo inserito!",
					"ATTENZIONE!",2);
	}
	
	/**
	 * Metodo che gestisce la textbox del prezzo.
	 * Se l'articolo in questione e' il primo del suo tipo il magazziniere ne inserisce il prezzo,
	 * altrimenti sarà letto un prezzo di un articolo dello stesso tipo dal database, e la textbox verra'
	 * resa non editabile
	 */
	private void gestioneFieldPrezzo() {
		NuovoIngresso panel = (NuovoIngresso) frame.getContentPane();

		String tipoSel = panel.getcBoxTipoArticolo().getSelectedItem().toString();
		double prezzo= ds.getPrezzoTipo(tipoSel);
		if (prezzo>0.00) {
			panel.getFieldPrezzo().setEditable(false);
			panel.setPrezzo(prezzo);
        }
        else {
        	panel.getFieldPrezzo().setText("0.00");
        	panel.getFieldPrezzo().setEditable(true);
        }
	}

}
