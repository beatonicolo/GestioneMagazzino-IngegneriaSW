package model.entities;

import model.dbcom.DataSource;

/**
 * Sotto-classe di tipo articolo. Rappresenta i tipi articolo una volta ordinati da un negozio.
 * Rispetto alla classe padre aggiunge il campo quantita' e un calcolo dinamico del prezzo
 * @author beato
 *
 */
public class TipoArticoloOr extends TipoArticolo {
	
	private int quantita;
	
	/**
	 * Costruttore che crea un oggetto TipoArticoloOr partendo da un TipoArticolo e una quantita'
	 * @param articolo tipo articolo che il negozio richiede
	 * @param quantita 
	 */
	public TipoArticoloOr(TipoArticolo articolo, int quantita) {
		super(articolo);
		this.quantita=quantita;
	}
	/**
	 * Costruttore che crea un oggetto TipoArticoloOr partendo da tutti i parametri che ne definiscono i campi,
	 * sia della classe padre che della classe figlio
	 * @param nome
	 * @param descrizione
	 * @param materiali
	 * @param sport
	 * @param quantita
	 */
	public TipoArticoloOr(String nome,String descrizione,String materiali,String sport,int quantita) {
		super(nome,descrizione,materiali,sport);
		this.quantita=quantita;
	}
	
	public int getQuantita() {return this.quantita;}
	
	/**
	 * metodo che richiede al database il prezzo degli articoli del suo stesso tipo e li moltiplica per quantita
	 * @return prezzo*quantita
	 */
	public double getPrezzo() {
		double prezzo= DataSource.getInstance().getPrezzoTipo(this.getNome());
		prezzo*=quantita;
		return prezzo;
	}
	
	/**
	 * metodo che verifica se due istanze della classe hanno lo stesso nome
	 * @param other altra istanza di TipoArticoloOr
	 * @return true se le due istanze hanno lo stesso nome, false altrimenti
	 */
	public boolean equal(TipoArticoloOr other) {
		if (this.getNome()==other.getNome())
			return true;
		else
			return false;
	}
	
	public void addQuantita(int nuovaQuantita) {
		this.quantita+=nuovaQuantita;
	}
}
