package controller;

import java.awt.event.ActionEvent;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import gui.pages.NuovoOrdine;
import gui.pages.OrdiniEffettuati;
import gui.resources.MyFrame;
import model.entities.Negozio;
import model.entities.Ordine;
import model.entities.TipoArticoloOr;

/**
 * Classe che implementa il listener che gestisce gli eventi generati dai componenti inseriti
 * nelle schermate dedicate ai negozi
 * @author beato
 *
 */
public class NegozioListener extends GlobalListener implements ListSelectionListener {	
	/**
	 * Costruttore listener per schermate dedicate ai negozi
	 * @param frame frame pricipale, usato per accedere ai componenti delle schermate
	 */
	public NegozioListener(MyFrame frame,Negozio negozio) {
		super(frame);
		this.negozioConnesso=negozio;
	}

	/**
	 * implementazione del metodo dell'interfaccia ActionListener che identifica il componente che ha generato l'evento e chiama il metodo
	 * che svolge le azioni richieste 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		String caller = btn.getText();
		navigazionePerformed(caller);// controllo se il generante è per navigazione

		if (caller.equals("Aggiungi")) {
			addCarrello();
		}
		else if (caller.equals("Elimina")) {
			removeCarrello();
		}
		else if (caller.equals("Conferma")) {
			confermaOrdine();
		}
	}
	/**
	 * implementazione del metodo dell'interfaccia ListSelectionListener che riceve la notifica di un cambio
	 * dell'elemento selezionato nella lista degli ordini effettuati da un negozio 
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			mostraDettagli();
		}
	}

	/**
	 * metodo che, nella schermata di consultazine degli ordini effettuati, mostra il dettaglio dell'ordine
	 * selezionato
	 */
	private void mostraDettagli() {
		OrdiniEffettuati panel = (OrdiniEffettuati) frame.getContentPane();

		Ordine ordine = panel.getListaOrdini().get(panel.getListOrdiniEffettuati().getSelectedIndex());
		panel.setTextArea(ordine.toString());
	}

	/**
	 * metodo che si occupa di aggiungere un tipo articolo ad un ordine in fase di creazione, gestendo
	 * la quantita' e il prezzo (se presente) del tipo articolo ordinato
	 */
	private void addCarrello () {
		NuovoOrdine panel = (NuovoOrdine) frame.getContentPane();

		DefaultTableModel carrelloModel = (DefaultTableModel) panel.getCarrello().getModel();
		
		int quantita;
		try {quantita=Integer.valueOf(panel.getFieldQuantita());}
		catch (NumberFormatException e) {quantita=0;}
		
		double prezzoTot=panel.getPrezzoTot();

		TipoArticoloOr articoloOr = new TipoArticoloOr(panel.getListaTipi().get(panel.getList().getSelectedIndex()),quantita);
		double prezzoOr = articoloOr.getPrezzo();


		List <TipoArticoloOr> listaArticoliOrdinati = panel.getListaArticoliOrdinati();

		if(quantita>0) {
			/* controllo che non si stia cercando di inserire nell'ordine un tipo articolo gia' inserito in precedenza
			 * in tal caso sommo quantita' e prezzo
			 */
			for (int i=0;i<listaArticoliOrdinati.size();i++){
				if(articoloOr.equal(listaArticoliOrdinati.get(i))) {
					listaArticoliOrdinati.get(i).addQuantita(articoloOr.getQuantita());
					carrelloModel.removeRow(i);
					carrelloModel.insertRow(i,new Object[] {listaArticoliOrdinati.get(i).getNome(),listaArticoliOrdinati.get(i).getQuantita(),String.format(Locale.ROOT,"%.2f",listaArticoliOrdinati.get(i).getPrezzo())+"\u20AC"});
					prezzoTot += prezzoOr;
					panel.setPrezzoTot(prezzoTot);
					panel.setPrice(prezzoTot);
					return;
				}		
			}

			listaArticoliOrdinati.add(articoloOr);
			carrelloModel.addRow(new Object[] {articoloOr.getNome(),articoloOr.getQuantita(),articoloOr.getPrezzo()+"\u20AC"});
			if (prezzoOr<=0.00) 
				JOptionPane.showMessageDialog(null,"Del tipo articolo selezionato non si conosce ancora il prezzo",
						"ATTENZIONE PREZZO NON DISPONIBILE!",2);

			prezzoTot += prezzoOr;
			panel.setPrezzoTot(prezzoTot);
			panel.setPrice(prezzoTot);
		}
	}

	/**
	 * metodo che si occupa di rimuovere un tipo articolo da un ordine in fase di crezione,
	 * modificandone di conseguenza anche il prezzo totale
	 */
	private void removeCarrello() {
		NuovoOrdine panel = (NuovoOrdine) frame.getContentPane();

		DefaultTableModel carrelloModel = (DefaultTableModel) panel.getCarrello().getModel();
		double prezzoTot=panel.getPrezzoTot();
		List <TipoArticoloOr> listaArticoliOrdinati = panel.getListaArticoliOrdinati();

		//controllo che ci sia una riga selezionata
		if(panel.getCarrello().getSelectedRow()>=0) {
			prezzoTot-=(listaArticoliOrdinati.get(panel.getCarrello().getSelectedRow()).getPrezzo());
			panel.setPrezzoTot(prezzoTot);
			listaArticoliOrdinati.remove(panel.getCarrello().getSelectedRow());
			carrelloModel.removeRow(panel.getCarrello().getSelectedRow());
			panel.setPrice(prezzoTot);
		}
	}

	/**
	 * metodo che che registra nel database l'ordine creato
	 */
	private void confermaOrdine() {
		NuovoOrdine panel = (NuovoOrdine) frame.getContentPane();

		List <TipoArticoloOr> listaArticoliOrdinati = panel.getListaArticoliOrdinati();
		//controlla che non si stia creando un ordine vuoto
		if(!listaArticoliOrdinati.isEmpty()) {			
			ds.inserimentoOrdine(new Ordine(ds.nextOrdine(),new Date(Calendar.getInstance().getTimeInMillis()),negozioConnesso.getCodFisc(),panel.getPrezzoTot(),listaArticoliOrdinati));

			JOptionPane.showMessageDialog(null,"Ordine effetuato con successo",
					"ORDINE EFFETTUATO",3);
			panel.clearAll();

		}
		else
			JOptionPane.showMessageDialog(null,"Attenzione l'ordine non contiene alcun tipo articolo,riprovare",
					"ATTENZIONE ORDINE VUOTO!",2);
	}

}
