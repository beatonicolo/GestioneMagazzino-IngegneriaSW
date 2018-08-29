package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import gui.pages.Home;
import gui.pages.InserimentoTipo;
import gui.pages.MovimentiInEntrata;
import gui.pages.MovimentiInUscita;
import gui.pages.NuovaUscita;
import gui.pages.NuovoIngresso;
import gui.pages.NuovoOrdine;
import gui.pages.OrdiniEffettuati;
import gui.pages.Spostamento;
import gui.resources.MyFrame;
import model.dbcom.DataSource;
import model.entities.Negozio;

/**
 * Classe che implementa il listener che definisce le funzionalita' utili alla navigazione e al login
 * @author beato
 */
public class GlobalListener implements ActionListener {

	protected MyFrame frame;
	protected Negozio negozioConnesso;
	protected DataSource ds;

	/**
	 * Costruttore listener login e navigazione
	 * @param frame frame principale, utilizzato per accedere ai componenti delle schermate
	 */
	public GlobalListener (MyFrame frame) {
		this.frame=  frame;
		this.ds = DataSource.getInstance();
	}
	/**
	 * ridefinizione del metodo chiamato alla generazione di un evento
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		navigazionePerformed(btn.getText());
	}
	/**
	 * metodo che identifica il componente che ha generato l'evento e chiama i metodi
	 * specifici corrispondenti alle azioni da eseguire
	 * @param caller nome del componente che ha generato l'evento
	 */
	protected void navigazionePerformed(String caller) {
		
		if (caller.equals("Login Negozi")) {
			doLoginNegozi();	
			ds.checkStorico();
		}

		else if (caller.equals("Login Magazzinieri")) {
			doLoginMagazzinieri();
			ds.checkStorico();
		}

		else if (caller.equals("Login Segreteria")){
			doLoginSegreteria();
			ds.checkStorico();
		}

		else if (caller.equals("Logout")){
			doLogout();
			ds.checkStorico();
		}
		else if (caller.equals("Spostamento"))
			goToSpostamento();

		else if (caller.equals("Nuovo Ingresso"))
			goToNuovoIngresso();

		else if (caller.equals("Nuova Uscita"))
			goToNuovaUscita();

		if (caller.equals("Nuovo Ordine")) {
			goToNuovoOrdine();
		}
		else if (caller.equals("Ordini Effettuati")) {
			goToOrdiniEffettuati();
		}
		else if (caller.equals("Inserimento Tipo Articolo")) {
			goToInserisciTipo();
		}
		else if (caller.equals("Uscite")) {
			goToMovimentiInUscita();
		}
		else if (caller.equals("Entrate")) {
			goToMovimentiInEntrata();
		}
	}

	/**
	 * Metodo che gestisce il login da parte dei negozi
	 */
	private void doLoginNegozi() {
		Home home = (Home) frame.getContentPane();
		String username=home.getUsername();
		String password=home.getPassword();
		boolean autenticato=ds.loginNegozio(username, password);
		if (autenticato) {
			negozioConnesso= ds.getNegozio(username);
			home.setMessaggioErrore("");
			goToOrdiniEffettuati();
		}
		else 
			home.setMessaggioErrore("Credenziali errate per login negozio");
	}

	/**
	 * Metodo che gestisce il login da parte dei magazzinieri
	 */
	private void doLoginMagazzinieri() {
		Home home = (Home) frame.getContentPane();
		String username=home.getUsername();
		String password=home.getPassword();
		boolean autenticato=ds.loginMagazziniere(username, password);
		if (autenticato) {
			home.setMessaggioErrore("");
			goToSpostamento();
		}
		else 
			home.setMessaggioErrore("Credenziali errate per login magazziniere");	
	}

	/**
	 * Metodo che gestisce il login da parte dei dipendeti della segreteria
	 */
	private void doLoginSegreteria() {
		Home home = (Home) frame.getContentPane();
		String username=home.getUsername();
		String password=home.getPassword();
		boolean autenticato=ds.loginSegreteria(username, password);
		if (autenticato) {
			home.setMessaggioErrore("");
			goToMovimentiInUscita();
		}
		else 
			home.setMessaggioErrore("Credenziali errate per login segreteria");	
	}

	/**
	 * Metodo che gestisce il logout
	 */
	private void doLogout() {
		frame.setContentPane(new Home(this));
		frame.validate();
	}

	/**
	 * metodo che rimanda alla schermata degli ordini effettuati
	 */
	private void goToOrdiniEffettuati() {
		frame.setContentPane(new OrdiniEffettuati(new NegozioListener(frame,negozioConnesso),negozioConnesso));
		frame.validate();
	}

	/**
	 * metodo che rimanda alla schermata di spostamento di un articolo
	 */
	private void goToSpostamento() {
		frame.setContentPane(new Spostamento(new MagazziniereListener(frame)));
		frame.validate();
	}

	/**
	 * metodo che rimanda alla schermata dei movimenti in uscita
	 */
	private void goToMovimentiInUscita() {
		frame.setContentPane(new MovimentiInUscita(new SegreteriaListener(frame)));
		frame.validate();
	}

	/**
	 * metodo che rimanda alla schermata di inserimento di un nuovo ordine
	 */
	private void goToNuovoOrdine() {
		frame.setContentPane(new NuovoOrdine(new NegozioListener(frame,negozioConnesso)));
		frame.validate();
	}

	/**
	 * metodo che rimanda alla schermata di inserimento del tipo articolo
	 */
	private void goToInserisciTipo() {
		frame.setContentPane(new InserimentoTipo(new SegreteriaListener(frame)));
		frame.validate();
	}

	/**
	 * metodo che rimanda alla schermata del riepigolo degli ingressi
	 */
	private void goToMovimentiInEntrata() {
		frame.setContentPane(new MovimentiInEntrata(new SegreteriaListener(frame)));
		frame.validate();
	}

	/**
	 * metodo che rimanda alla schermata di inserimento di un nuovo ingresso
	 */
	private void goToNuovoIngresso() {
		frame.setContentPane(new NuovoIngresso(new MagazziniereListener(frame)));
		frame.validate();
	}

	/**
	 * metodo che rimanda alla schermata di inserimento di una nuova uscita
	*/
	private void goToNuovaUscita() {
		frame.setContentPane(new NuovaUscita(new MagazziniereListener(frame)));
		frame.validate();
	}
}

