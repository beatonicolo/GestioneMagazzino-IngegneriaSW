package model.entities;

import java.util.Locale;

/**
 * Classe che descrive un articolo
 * @author beato
 *
 */
public class Articolo {
	
	private String codice;
	private double prezzo;
	private String DataProduzione;
	private String tipoArticoloRef;
	private String posizione;
	private String ingressoRef;
	
	/**
	 * Costruttore completo invocato in seguito alle lettura dal database
	 * @param codice
	 * @param prezzo
	 * @param produzione data di produzione
	 * @param posizione
	 * @param tipoRef tipo dell'articolo
	 * @param ingressoRef 
	 */
	public Articolo(String codice,double prezzo,String produzione,String posizione,String tipoRef,String ingressoRef) {
		this.codice=codice;
		this.prezzo=prezzo;
		this.DataProduzione=produzione;
		this.tipoArticoloRef=tipoRef;
		this.posizione=posizione;
		this.ingressoRef=ingressoRef;
	}
	
	/**
	 * Costruttore semplice invocato al momento dell'inserimento di un ingresso di artcoli
	 * @param prezzo
	 * @param produzione data di produzione
	 * @param posizione
	 * @param tipoRef
	 * @param ingressoRef
	 */
	public Articolo(double prezzo,String produzione,String posizione,String tipoRef,String ingressoRef) {
		this.prezzo=prezzo;
		this.DataProduzione=produzione;
		this.tipoArticoloRef=tipoRef;
		this.posizione=posizione;
		this.ingressoRef=ingressoRef;
	}
	
	public String getCodice() {return codice;}

	public double getPrezzo() {return prezzo;}

	public String getDataProduzione() {return DataProduzione;}

	public String getTipoArticoloRef() {return tipoArticoloRef;}
	
	public String getPosizione() {return this.posizione;}
	public String getIngressoRef() {return this.ingressoRef;}
	
	@Override
	public String toString() {
		return  "\n\n		Articolo: "+this.codice+"\n"+
				"		Tipo:"+this.tipoArticoloRef+"\n"+
				"		Data produzione:"+this.DataProduzione+"\n"+
				"		Posizione:"+this.posizione+"\n"+
				"		Prezzo:"+String.format(Locale.ROOT, "%.2f", this.prezzo)+"\n";
	}
}
