package model.entities;

import java.sql.Date;
import java.util.List;
import java.util.Locale;

/**
 * Classe che rappresenta un ordine da parte di un negozio.
 * @author beato
 *
 */
public class Ordine {
	
	private String codice;
	private Date data;
	private String negozio;
	private double prezzoTotale;
	private List <TipoArticoloOr> articoliOrdinati;
	
	/**
	 * Costruttore ordine
	 * @param codice codice univoco dell'ordine
	 * @param data data in cui l'ordine viene effettuato
	 * @param negozio negozio che effettua l'ordine
	 * @param prezzo importo totale dell'ordine
	 * @param articoliOrdinati lista degli articoli ordinati
	 */
	public Ordine (String codice,Date data,String negozio,double prezzo,List<TipoArticoloOr> articoliOrdinati) {
		this.codice=codice;
		this.data=data;
		this.negozio=negozio;
		this.prezzoTotale=prezzo;
		this.articoliOrdinati=articoliOrdinati;
	}
	
	public String getCodice() {return codice;}	
	public Date getData() {return this.data;}
	public String getNegozio() {return this.negozio;}
	public double getPrezzoTotale () {return this.prezzoTotale;}
	public List<TipoArticoloOr> getListaArticoliOrdinati (){return this.articoliOrdinati;}
	
	@Override
	public String toString() {
		
		String listaTipoArticolo="";
		
		for(int i=0;i<articoliOrdinati.size();i++)
			listaTipoArticolo +="		"+articoliOrdinati.get(i).getNome()+" x "+articoliOrdinati.get(i).getQuantita()+"\n";
		
		String result="Dettagli Ordine:\n"+
				"	Codice: "+this.codice+"\n"+
				"	Data: "+this.data+"\n"+
				"	Negozio:"+this.negozio+"\n"+
				"	Prezzo totale:"+String.format(Locale.ROOT,"%.2f",this.prezzoTotale)+"\n"+
				"	Tipi Articolo:\n"+listaTipoArticolo;
		return result;		
	}

}
