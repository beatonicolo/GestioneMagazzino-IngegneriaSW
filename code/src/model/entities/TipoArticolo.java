package model.entities;

/**
 * Classe che definisce i tipi di articolo presenti in magazzino
 * @author beato
 *
 */
public class TipoArticolo {
	
	private String nome;
	private String descrizione;
	private String materiali;
	private String sport;
	
	/**
	 * Costruttore elementare, riceve tutti i parametri richiesti per completare i campi e crea un oggetto
	 * @param nome
	 * @param descrizione
	 * @param materiali
	 * @param sport
	 */
	public TipoArticolo(String nome,String descrizione,String materiali,String sport) {
		this.nome=nome;
		this.descrizione=descrizione;
		this.materiali=materiali;
		this.sport=sport;
	}

	/**
	 * Costruttore che viene invocato dalla sotto-classe, crea una copia di un altro oggetto
	 * @param other un altro tipo articolo
	 */
	public TipoArticolo(TipoArticolo other) {
		this.nome=other.nome;
		this.descrizione=other.descrizione;
		this.materiali=other.materiali;
		this.sport=other.sport;
	}

	public String getNome() {return nome;}
	
	public String getDescrizione() {return descrizione;}
	
	public String getMateriali() {return materiali;}

	public String getSport() {return sport;}	
}
