package model.entities;

import java.sql.Date;
import java.util.List;

/**
 * Classe che definisce un'uscita dal magazzino
 * @author beato
 *
 */
public class Uscita {

	private String bolla;
	private Date data;
	private String spedizioniere;
	private String ordineRef;
	private String negozio;
	private List<Articolo> articoliOut;
	
	/**
	 * Costruttore uscita
	 * @param bolla numero bolla progressivo e univoco nell'intervallo di un anno solare
	 * @param data
	 * @param spedizioniere
	 * @param ordineRef riferimento all'ordine
	 * @param negozio
	 * @param articoliOut lista degli articoli in uscita
	 */
	public Uscita (String bolla,Date data,String spedizioniere,String ordineRef,String negozio,List<Articolo> articoliOut) {
		this.bolla=bolla;
		this.data=data;
		this.spedizioniere=spedizioniere;
		this.ordineRef=ordineRef;
		this.negozio=negozio;
		this.articoliOut=articoliOut;
	}
	
	public String getBolla() {return this.bolla;}
	public Date getData() {return this.data;}
	public String getSpedizioniere() {return this.spedizioniere;}
	public String getOrdinereRef() {return this.ordineRef;}
	public String getNegozio() {return this.negozio;}
	public List<Articolo> getArticoliOut() {return this.articoliOut;}
	
	public String toString() {
		
		String listaArticoli="";
		for(int i=0;i<articoliOut.size();i++)
			listaArticoli +="		"+articoliOut.get(i).getCodice()+"\n";
		
		String result="Dettagli Uscita:\n"+
				"	Data: "+this.data+"\n"+
				"	Numero Bolla: "+this.bolla+"\n"+
				"	Spedizioniere:"+this.spedizioniere+"\n"+
				"	Riferimento Ordine:"+this.ordineRef+"\n"+
				"	Lista Articoli:\n"+listaArticoli;
		return result;
	}
}
