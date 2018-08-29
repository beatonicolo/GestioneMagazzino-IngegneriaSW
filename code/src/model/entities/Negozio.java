package model.entities;

/**
 * Classe che descrive un negozio
 * @author beato
 *
 */
public class Negozio {

	private String codFisc;
	private String nome;
	private String indirizzo;
	private String citta;
	private String username;
	private String password;
	
	/**
	 * Costruttore che viene invocato quando viene effettuato il login.
	 * @param codFisc codice fiscale
	 * @param nome nome del negozio
	 * @param indirizzo
	 * @param citta
	 * @param username
	 * @param password
	 */
	public Negozio (String codFisc,String nome,String indirizzo,String citta,String username,String password) {
		this.codFisc=codFisc;
		this.nome=nome;
		this.indirizzo=indirizzo;
		this.citta=citta;
		this.username=username;
		this.password=password;
	}
	
	public String getCodFisc() {return codFisc;}	
	
}
