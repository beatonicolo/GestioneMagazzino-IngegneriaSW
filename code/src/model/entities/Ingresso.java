package model.entities;

import java.sql.Date;
import java.util.List;

/**
 * Classe che descrive un ingresso di articoli in magazzino. Ovvero una consegna.
 * @author beato
 *
 */
public class Ingresso {
	
	private String codice;
	private Date data;	
	private List<Articolo> listaArticoliIn;
	
	/**
	 * Costruttore Ingresso
	 * @param codice il codice univoco dell'ingresso
	 * @param data la data in cui è stato registrato l'ingresso
	 * @param listaArticoliIn lista degli articoli compresi nell'ingresso
	 */
	public Ingresso (String codice,Date data,List<Articolo> listaArticoliIn) {
		this.codice=codice;
		this.data=data;
		this.listaArticoliIn=listaArticoliIn;
	}
	
	public String getCodice() {return this.codice;}
	public Date getData() {return this.data;}
	public List<Articolo> getListaArticoliIn () {return this.listaArticoliIn;}

	@Override
	public String toString() {
		String out="Dettagli Ingresso: \n"+
					"	Codice Ingresso: "+this.codice+"\n"+
					"	Data Ingresso: "+this.data+"\n"+
					"	Lista Articoli:\n";;
		 for (int i=0;i<listaArticoliIn.size();i++)
				out += listaArticoliIn.get(i).toString();
		return out;
	}
}
