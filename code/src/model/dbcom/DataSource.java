package model.dbcom;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import model.entities.Articolo;
import model.entities.Ingresso;
import model.entities.Negozio;
import model.entities.Ordine;
import model.entities.TipoArticolo;
import model.entities.TipoArticoloOr;
import model.entities.Uscita;

/**
 * Classe che si occupa della comunicazione con il database. Dalla connessione alla lettura dei dati.
 * Offre dei metodi pubblici che restutiscono i dati letti.
 * @author beato
 *
 */
public final class DataSource {

	// Nome del driver JDBC, URL database e credenziali
	private static final String JDBC_DRIVER = "org.postgresql.Driver";  
	private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
	private static final String USER = "postgres";
	private static final String PASS = "password";

	//QUERIES
	private static final String LISTATIPISTR="SELECT DISTINCT nomeTipo FROM tipoArticolo";
	private static final String LISTATIPI="SELECT nomeTipo,descrizione,materiale,nomeSportE FROM tipoArticolo";
	private static final String MODPOSIZIONE="UPDATE articolo SET posizione = LOWER(?) WHERE CodiceArticolo = ? ";
	private static final String LASTORDINE="SELECT CodiceOrdine FROM ordine ORDER BY CodiceOrdine::int DESC";
	private static final String LASTINGRESSO="SELECT CodiceIngresso FROM ingresso ORDER BY CodiceIngresso::int DESC";
	private static final String INSERTARTICOLO="INSERT INTO articolo (CodiceArticolo, Prezzo, DataProd, Posizione, TipoArt, CodIngresso) VALUES (LOWER(?),?,?,LOWER(?),LOWER(?),LOWER(?))";
	private static final String LASTARTICOLO="SELECT CodiceArticolo FROM articolo ORDER BY CodiceArticolo::int DESC";
	private static final String NEXTUSCITA="SELECT MAX(date_part('year', DataUscita)), MAX(NumBolla::int) FROM uscita WHERE date_part('year',CURRENT_DATE) = date_part('year', DataUscita)";
	private static final String VERIFICACODARTICOLO="SELECT count(*) FROM articolo WHERE CodiceArticolo = ? AND bollaU is NULL;";
	private static final String LOGINNEGOZIO="SELECT count(*) FROM negozio WHERE Username = LOWER(?) AND Password = ?";
	private static final String GETARTICOLO="SELECT CodiceArticolo,prezzo,dataProd,posizione,tipoArt,codIngresso FROM articolo WHERE CodiceArticolo = ? ;";
	private static final String LOGINMAGAZZINIERE="SELECT count(*) FROM magazziniere WHERE UsernameM = LOWER(?) AND PasswordM = ?";
	private static final String LOGINSEGRETERIA="SELECT count(*) FROM segreteria WHERE UsernameS = LOWER(?) AND PasswordS = ?";
	private static final String GETNEGOZIO="SELECT CF, Nome, Indirizzo, Citta, Username, Password FROM Negozio WHERE Username = LOWER(?) ;";
	private static final String LISTAORDININEGOZIO="SELECT CodiceOrdine, DataOrdine, PrezzoTotale FROM ordine WHERE CFNegozio = LOWER(?)";
	private static final String LISTATIPIORDINE="SELECT NomeT, Quantita FROM composto WHERE CodiceO = ? ";
	private static final String GETTIPOART="SELECT nomeTipo, Descrizione, Materiale, NomeSportE FROM tipoArticolo WHERE nomeTipo = LOWER(?)";
	private static final String GETCFNEGOZIO="SELECT CFNegozio FROM ordine WHERE CodiceOrdine = ?";
	private static final String INSERTUSCITA="INSERT INTO uscita (DataUscita, NumBolla, NomeSped, RifOrdine) VALUES (?,LOWER(?),LOWER(?),?)";
	private static final String UPDATEARTICOLOUSCITO="UPDATE articolo SET bollaU = LOWER(?) WHERE codiceArticolo = ?";
	private static final String INSERTINGRESSO="INSERT INTO ingresso (codiceIngresso, dataIngresso) VALUES (LOWER(?),?)";
	private static final String GETCODORDINEUSCITA="SELECT rifOrdine FROM Uscita WHERE NumBolla=? AND DataUscita=?";
	private static final String GETORDINE="SELECT CodiceOrdine, DataOrdine, CFNegozio, PrezzoTotale FROM ordine WHERE CodiceOrdine = ?";
	private static final String LISTAUSCITE="SELECT DataUscita, NumBolla, NomeSped, RifOrdine FROM uscita";
	private static final String LISTAARTICOLIUSCITA="SELECT CodiceArticolo, Prezzo, DataProd, Posizione, TipoArt, CodIngresso FROM Articolo WHERE bollaU=?";
	private static final String VERIFICANOMETIPOART="SELECT count(*) FROM tipoarticolo WHERE nometipo = LOWER(?);";
	private static final String INSERTTIPOART="INSERT INTO tipoArticolo (nomeTipo, Descrizione, Materiale, NomeSportE) VALUES (LOWER(?),LOWER(?),LOWER(?),LOWER(?))";
	private static final String LISTASPORT="SELECT nomeSport FROM sport;";
	private static final String LISTASPEDIZIONIERI="SELECT nomeS FROM spedizioniere;";
	private static final String LISTAARTICOLIINGRESSO="SELECT * FROM articolo WHERE codIngresso = ?;";
	private static final String LISTAINGRESSI="SELECT * FROM Ingresso;";
	private static final String PREZZOTIPOART="SELECT prezzo FROM articolo WHERE tipoArt = LOWER(?) ;";
	private static final String VERIFICACFNEGOZIO="SELECT count(*) FROM negozio WHERE CF = LOWER(?);";
	private static final String LISTAUSCITENEGOZIO="SELECT DataUscita, NumBolla, NomeSped, RifOrdine FROM uscita INNER JOIN ordine On uscita.RifOrdine=ordine.CodiceOrdine WHERE CFNegozio =  LOWER(?)";
	private static final String INSERTORDINE="INSERT INTO ordine (codiceOrdine,dataOrdine,cfNegozio,prezzoTotale) VALUES (?,?,LOWER(?),?)";
	private static final String INSERTCOMPOSTO="INSERT INTO composto (codiceO,nomeT,quantita) VALUES (?,LOWER(?),?)";
	private static final String LISTAORDININONEVASI="SELECT CodiceOrdine, DataOrdine, CFNegozio, PrezzoTotale FROM Ordine WHERE CodiceOrdine IN (SELECT CodiceOrdine FROM Ordine EXCEPT SELECT RifOrdine FROM Uscita) ORDER BY CodiceOrdine::int";
	private static final String LASTAGGIORNAMENTOSTORICO="SELECT MAX(mese) AS meseUltimo,anno FROM storico WHERE anno = (SELECT MAX(anno) FROM storico) GROUP BY anno;";
	private static final String INSERTSTORICO ="INSERT INTO storico (TipoA, Mese, Anno, Entrate, Uscite) VALUES (LOWER(?), date_part('month',CURRENT_DATE - ? * interval '1 month'), date_part('year',CURRENT_DATE - ? * interval '1 month'),?,?);";
	private static final String GETENTRATETIPOSTORICO ="SELECT count(*) FROM articolo INNER JOIN ingresso ON ingresso.codiceingresso = articolo.codingresso WHERE TipoArt = LOWER(?) AND date_part('month', dataingresso) = date_part('month', current_date - ? * interval '1 month') AND date_part('year', dataingresso) = date_part('year', current_date - ? * interval '1 month');";
	private static final String GETUSCITETIPOSTORICO= "SELECT count(bollaU) FROM articolo INNER JOIN uscita on Articolo.bollaU=Uscita.numBOlla WHERE Articolo.TipoArt = LOWER(?) AND date_part('month', Uscita.dataUscita) = date_part('month', current_date - ? * interval '1 month') AND date_part('year', uscita.dataUscita) = date_part('year', current_date - ? * interval '1 month');";
	private static final String LISTAARTICOLIEVADIBILI="SELECT CodiceArticolo,prezzo,dataProd,posizione,tipoArt,codIngresso FROM articolo WHERE TipoArt IN (SELECT NomeT FROM composto WHERE CodiceO = ?) AND bollaU is NULL ORDER BY tipoArt, CodiceArticolo::int";
	private static final String VERIFICAGIACENZA="SELECT COUNT(CodiceArticolo) FROM articolo WHERE TipoArt = ? AND bollaU is NULL";
	private static final String LISTACFNEGOZI="SELECT CF from negozio";
	
	private Connection connessioneDB=null;
	private static DataSource instance=null;
	
	/**
	 * Costruttore privato per evitare che la classe venga instanziata
	 */
	private DataSource() {
		try {Class.forName(JDBC_DRIVER);} catch (ClassNotFoundException e) {e.printStackTrace();}
		try {
			connessioneDB = DriverManager.getConnection( DB_URL, USER,PASS);
		}catch( SQLException sqle ) {sqle.printStackTrace();}
	}
	
	/**
	 * metodo che che si occupa di gestire le richieste di un'istanza della classe.
	 * l'unica istanza esistente della classe verrà creata alla prima invocazione di tale metodo
	 * @return istanza attiva della classe
	 */
	public static DataSource getInstance() {
		if (instance == null)
			instance= new DataSource();
		return instance;
	}
	
	/**
	 * Metodo che ritorna la lista di tutti i tipi articolo
	 * @return lista di stringhe contenente i nomi dei tipi articolo
	 */
	public List <String> getStringListaTipi() {
		Statement stmt = null;
		ResultSet rs = null;
		List <String> listaTipi = new ArrayList <String> ();

		try {
			stmt = connessioneDB.createStatement();
			rs = stmt.executeQuery( LISTATIPISTR );
			while ( rs.next() ) 
				listaTipi.add(rs.getString("nomeTipo"));
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null) 
					stmt.close();
			}catch (Exception e) {e.printStackTrace();}
		}	
		return listaTipi;
	}

	/**
	 * metodo che esegue un update sulla tabella articolo modificandone la posizione in magazzino
	 * @param posizione nuova posizione
	 * @param codice codice articolo
	 */
	public void modPosizione (String posizione, String codice) {
		PreparedStatement pstat = null;

		try {
			pstat = connessioneDB.prepareStatement(MODPOSIZIONE);
			pstat.setString(1, posizione);
			pstat.setString(2, codice);
			pstat.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if (pstat != null)
					pstat.close();
			}catch (Exception e) {e.printStackTrace();}
		}
	}

	/**
	 * Metodo che calcola il codice da assegnare al prossimo ordine leggendo l'ultimo inserito
	 * ed incrementandolo
	 * @return stringa contenente il codice del nuovo ordine
	 */
	public String nextOrdine() {

		String result="";
		int n=0;
		ResultSet rs = null;
		Statement stat = null;	
		try {
			stat = connessioneDB.createStatement();
			rs = stat.executeQuery(LASTORDINE);
			rs.next();
			result = rs.getString("CodiceOrdine");
		}catch (SQLException e) {e.printStackTrace();} 
		finally {
			try {
				if (rs != null)
					rs.close();
				if (stat != null)
					stat.close();
			}catch (Exception e) {e.printStackTrace();}
		}	
		n = Integer.parseInt(result);
		return String.valueOf(++n);
	}

	/**
	 * Metodo che calcola il codice da assegnare al prossimo articolo leggendo l'ultimo inserito
	 * ed incrementandolo
	 * @return stringa contenente il codice da assegnare al nuovo articolo
	 */
	private String nextArticolo() {

		String result="";
		int n=0;
		ResultSet rs = null;
		Statement stat = null;	
		try {
			stat = connessioneDB.createStatement();
			rs = stat.executeQuery(LASTARTICOLO);
			rs.next();
			result = rs.getString("CodiceArticolo");
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if (rs != null)
					rs.close();
				if (stat != null) 
					stat.close();
			}catch (Exception e) {	e.printStackTrace();}
		}	
		n = Integer.parseInt(result);
		return String.valueOf(++n);
	}

	/**
	 * Metodo che calcola il codice da assegnare al prossimo ingresso leggendo l'ultimo inserito
	 * ed incrementandolo
	 * @return stringa contenente il codice da assegnare al nuovo ingresso
	 */
	public String nextIngresso() {

		String result="";
		int n=0;
		ResultSet rs = null;
		Statement stat = null;	
		try {
			stat = connessioneDB.createStatement();
			rs = stat.executeQuery(LASTINGRESSO);
			rs.next();
			result = rs.getString("CodiceIngresso");
		}catch (SQLException e) {e.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (stat != null) 
					stat.close();
			}catch (Exception e) {e.printStackTrace();}
		}	
		n = Integer.parseInt(result);
		return String.valueOf(++n);
	}

	/**
	 * Metodo che calcola il codice da assegnare alla prossima uscita leggendo l'ultima inserita
	 * ed incrementandone il suo numerobolla. Ogni anno riparte da 1
	 * @return stringa contenente il codice da assegnare al nuovo articolo
	 */
	public String nextUscita() {

		ResultSet rs = null;
		Statement stat = null;
		String anno="";
		String numbolla="";
		int dat=0;
		int num=0;
		int year = Calendar.getInstance().get(Calendar.YEAR);

		try {
			stat = connessioneDB.createStatement();
			rs = stat.executeQuery(NEXTUSCITA);
			rs.next();
			if ( rs.getString(1) != null ) {
				anno = rs.getString(1);
				numbolla = rs.getString(2); }
			else {
				return "1"; }
		}catch (SQLException e) {e.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (stat != null)
					stat.close();
			}catch (Exception e) {e.printStackTrace();}
		}	
		dat = Integer.parseInt(anno);
		num = Integer.parseInt(numbolla);
		if (dat == year)
			num++;
		else 
			num=1;
		return String.valueOf(num);
	}

	/**
	 * Metodo che verifica che il codice di un articolo sia corretto: sia presente nel sistema e in magazzino
	 * @param codice codice da vericare
	 * @return true se corretto
	 */
	public boolean verificaArticolo(String codice) {

		boolean esito = false;
		int ris = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = connessioneDB.prepareStatement( VERIFICACODARTICOLO );
			pstmt.clearParameters();
			pstmt.setString( 1, codice );
			rs = pstmt.executeQuery();
			rs.next();
			ris = rs.getInt("count");
			if (ris > 0 ) {
				esito=true;	
			}
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				}
			catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return esito;
	}

	/**
	 * Metodo che controlla il login dei negozio, ovvero se le credenziali inserite corrispondono a quelle
	 * memorizzate nel database
	 * @param user username
	 * @param pass password
	 * @return true se le credenziali sono corrette
	 */
	public boolean loginNegozio(String user, String pass) {

		boolean esito = false;
		int ris = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = connessioneDB.prepareStatement( LOGINNEGOZIO );
			pstmt.clearParameters();
			pstmt.setString( 1, user );
			pstmt.setString( 2, pass );
			rs = pstmt.executeQuery();
			rs.next();
			ris = rs.getInt("count");
			if (ris > 0 ) {
				esito=true;	
			}
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}
			catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return esito;
	}

	/**
	 * Metodo che controlla il login dei magazzinieri, ovvero se le credenziali inserite corrispondono a quelle
	 * memorizzate nel database
	 * @param user username
	 * @param pass password
	 * @return true se le credenziali sono corrette
	 */
	public boolean loginMagazziniere(String user, String pass) {

		boolean esito = false;
		int ris = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = connessioneDB.prepareStatement( LOGINMAGAZZINIERE );
			pstmt.clearParameters();
			pstmt.setString( 1, user );
			pstmt.setString( 2, pass );
			rs = pstmt.executeQuery();
			rs.next();
			ris = rs.getInt("count");
			if (ris > 0 ) {
				esito=true;	
			}
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();}
			catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return esito;
	}

	/**
	 * Metodo che controlla il login della segreteria, ovvero se le credenziali inserite corrispondono a quelle
	 * memorizzate nel database
	 * @param user username
	 * @param pass password
	 * @return true se le credenziali sono corrette
	 */
	public boolean loginSegreteria(String user, String pass) {

		boolean esito = false;
		int ris = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = connessioneDB.prepareStatement( LOGINSEGRETERIA );
			pstmt.clearParameters();
			pstmt.setString( 1, user );
			pstmt.setString( 2, pass );
			rs = pstmt.executeQuery();
			rs.next();
			ris = rs.getInt("count");
			if (ris > 0 ) {
				esito=true;	
			}
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}
			catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return esito;
	}

	/**
	 * Metodo che si occupa di leggere tutti gli atributi di un negozio e di crearne un oggetto Negozio da
	 * restituire al chiamante
	 * @param user username del negozio da recuperare
	 * @return Negozio, negozio richiesto
	 */
	public Negozio getNegozio(String user) {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Negozio result = null;

		try {
			pstmt = connessioneDB.prepareStatement( GETNEGOZIO );
			pstmt.clearParameters();
			pstmt.setString( 1, user );
			rs = pstmt.executeQuery();
			rs.next();
			result=new Negozio(rs.getString("CF"),rs.getString("nome"),rs.getString("indirizzo"),rs.getString("citta"),rs.getString("username"), rs.getString("password"));
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return result;
	}

	/**
	 * Metodo che recupera recupera tutti i tipi articolo presenti nel database e ne crea una lista
	 * @return List of TipoArticolo, lista di tutti i tipi articolo
	 */
	public List <TipoArticolo> getListaTipi() {
		Statement stmt = null;
		ResultSet rs = null;
		List <TipoArticolo> lista = new ArrayList <TipoArticolo>();

		try {
			stmt = connessioneDB.createStatement();
			rs = stmt.executeQuery( LISTATIPI );
			while ( rs.next() ) {
				String nome = rs.getString("nomeTipo");
				String descrizione = rs.getString("descrizione");
				String materiale = rs.getString("materiale");
				String sport = rs.getString("nomeSportE");
				lista.add(new TipoArticolo(nome,descrizione,materiale,sport));
			}
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return lista;
	}

	/**
	 * Metodo che dato un codice articolo ne recupera tutti gli attributi e crea con questi un oggetto Articolo
	 * @param codice codice dell'articolo da recuperare
	 * @return Articolo, l'articolo richiesto
	 */
	public Articolo getArticolo(String codice) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Articolo result = null;

		try {
			pstmt = connessioneDB.prepareStatement( GETARTICOLO );
			pstmt.clearParameters();
			pstmt.setString( 1, codice );
			rs = pstmt.executeQuery();
			rs.next();			
			result=new Articolo(rs.getString("CodiceArticolo"),rs.getDouble("prezzo"),rs.getString("dataProd"),rs.getString("posizione"), rs.getString("tipoArt"),rs.getString("codIngresso"));
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
		}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return result;
	}

	/**
	 * Metodo che ritorna una lista di tutti gli ordini già effettuati da un negozio
	 * @param codFisc codice fiscale negozio
	 * @return List of Ordine, ordini effetutati dal negozio
	 */
	public List<Ordine> getListaOrdiniNegozio(String codFisc) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Ordine> lista = new ArrayList<Ordine>();
		List<TipoArticoloOr> listaProd;

		try {
			pstmt = connessioneDB.prepareStatement( LISTAORDININEGOZIO );
			pstmt.clearParameters();
			pstmt.setString( 1, codFisc );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {	
				String codOrdine = rs.getString("CodiceOrdine");
				Date dataOrdine = rs.getDate("DataOrdine");
				double prezzo = rs.getDouble("prezzoTotale");
				listaProd = getListaArticoliOrdine(codOrdine);
				lista.add(new Ordine(codOrdine,dataOrdine,codFisc,prezzo,listaProd));
			} 
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return lista;
	}

	/**
	 * Metodo che ritorna una lista contenente tutti i tipi di articolo che compongono un ordine
	 * @param codice codice dell'ordine
	 * @return List of TipoArticoloOr, lista tipi articolo dell'ordine
	 */
	private List<TipoArticoloOr> getListaArticoliOrdine(String codice) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<TipoArticoloOr> lista = new ArrayList<TipoArticoloOr>();

		try {
			pstmt = connessioneDB.prepareStatement( LISTATIPIORDINE );
			pstmt.clearParameters();
			pstmt.setString( 1, codice );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {		
				TipoArticolo TA = recuperaTipoArt(rs.getString("NomeT"));
				lista.add(new TipoArticoloOr(TA,rs.getInt("Quantita")));
			} 
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return lista;
	}

	/**
	 * Metodo che dato un nome recuera tutti gli attributi di un tipo articolo
	 * @param nome nome tipo articolo
	 * @return tipo articolo richiesto
	 */
	private TipoArticolo recuperaTipoArt(String nome){

		TipoArticolo tipo = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = connessioneDB.prepareStatement( GETTIPOART );
			pstmt.clearParameters();
			pstmt.setString( 1, nome );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {	
				tipo = new TipoArticolo(rs.getString("nomeTipo"),rs.getString("Descrizione"),rs.getString("Materiale"),rs.getString("NomeSportE"));
			} 
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return tipo;
	}

	/**
	 * Metodo che dato un codice ordine recupera il codice fiscale del negozio che lo aveva creato
	 * @param ordine codice dell'ordine
	 * @return String, codice fiscale negozio
	 */
	public String recuperaCFNegozio(String ordine){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String ris = null;

		try {
			pstmt = connessioneDB.prepareStatement( GETCFNEGOZIO );
			pstmt.clearParameters();
			pstmt.setString( 1, ordine );
			rs = pstmt.executeQuery();
			rs.next();
			ris = rs.getString("CFNegozio");
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return ris;
	}

	/**
	 * Metodo che si occupa di registrare nel database una nuova uscita
	 * @param uscita l'uscita da registrare
	 */
	public void inserimentoUscita (Uscita uscita) {
		PreparedStatement pstmt = null;

		try {
			pstmt = connessioneDB.prepareStatement( INSERTUSCITA );
			pstmt.clearParameters();
			//inserisco uscita
			pstmt.setDate( 1, uscita.getData());
			pstmt.setString(2, uscita.getBolla());
			pstmt.setString(3, uscita.getSpedizioniere());
			pstmt.setString(4, uscita.getOrdinereRef());
			pstmt.executeUpdate();

			try {
				if (pstmt != null)
					pstmt.close();}
			catch( SQLException sqle1 ) {sqle1.printStackTrace();}
			
			pstmt = connessioneDB.prepareStatement( UPDATEARTICOLOUSCITO );
			pstmt.clearParameters();

			//faccio update su articoli
			List<Articolo> lista=uscita.getArticoliOut();
			for (int i=0;i<lista.size();i++) {
				Articolo articolo = lista.get(i);;
				pstmt.setString(1, uscita.getBolla());
				pstmt.setString(2, articolo.getCodice());
				pstmt.executeUpdate();
				pstmt.clearParameters();
			}		
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (pstmt != null)
					pstmt.close();
				}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
	}

	/**
	 * metodo che si occupa dell'inserimento di un nuovo ingresso
	 * @param ingresso nuovo ingresso da inserire
	 */
	public void inserimentoIngresso (Ingresso ingresso) {
		PreparedStatement pstmt = null;

		try {
			pstmt = connessioneDB.prepareStatement( INSERTINGRESSO );
			pstmt.clearParameters();
			//inserisco ingresso
			pstmt.setString( 1, ingresso.getCodice());
			pstmt.setDate(2, ingresso.getData());
			pstmt.executeUpdate();
			try {
				if (pstmt != null)
					pstmt.close();}
			catch( SQLException sqle1 ) {sqle1.printStackTrace();}
			pstmt = connessioneDB.prepareStatement( INSERTARTICOLO );
			//inserisco articoli
			List<Articolo> lista=ingresso.getListaArticoliIn();
			for (int i=0;i<lista.size();i++) {
				Articolo articolo = lista.get(i);
				//preparo statement e inserisco articolo
				pstmt.clearParameters();
				pstmt.setString(1, nextArticolo());
				pstmt.setDouble(2, articolo.getPrezzo());
				pstmt.setString(3, articolo.getDataProduzione());
				pstmt.setString(4, articolo.getPosizione());
				pstmt.setString(5, articolo.getTipoArticoloRef());
				pstmt.setString(6, articolo.getIngressoRef());
				pstmt.executeUpdate();
			}		
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (pstmt != null)
					pstmt.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
	}

	/**
	 * Metodo dagli attributi primari di un'uscita ne recupera l'ordine corrispondente
	 * @param numbolla numero bolla
	 * @param datau data dell'uscita
	 * @return Ordine, l'ordine a cui si rirferisce l'uscita
	 */
	public Ordine getOrdineDaUscita(String numbolla, Date datau) {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Ordine ord = null;
		String codice = getCodOrdineUscita(numbolla, datau);

		try {
			pstmt = connessioneDB.prepareStatement( GETORDINE );
			pstmt.clearParameters();
			pstmt.setString( 1, codice );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {	
				String codOrdine = rs.getString("CodiceOrdine");
				Date dataOrdine = rs.getDate("DataOrdine");
				double prezzoTotale = rs.getDouble("prezzoTotale");
				String negozio = rs.getString("CFNegozio");
				List<TipoArticoloOr> lista = getListaArticoliOrdine(codOrdine);
				ord = new Ordine(codOrdine,dataOrdine,negozio,prezzoTotale,lista);
			} 
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}
			catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return ord;
	}

	/**
	 * Metodo che ritorna il codice dell'ordine corrispondente all'uscita
	 * @param numbolla numero di bolla
	 * @param data data dell'uscita
	 * @return String, codice dell'ordine a cui si riferisce l'uscita
	 */
	private String getCodOrdineUscita(String numbolla, Date data) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String ris = null;

		try {
			pstmt = connessioneDB.prepareStatement( GETCODORDINEUSCITA );
			pstmt.clearParameters();
			pstmt.setString( 1, numbolla );
			pstmt.setDate( 2, data );
			rs = pstmt.executeQuery();
			rs.next();
			ris = rs.getString("rifOrdine");
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}
			catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return ris;
	}

	/**
	 * Metodo che ritorna la lista di tutte le uscite
	 * @return List of Uscita, lista di tute le uscite
	 */
	public List<Uscita> getListaUscite(){
		Statement stat = null;
		ResultSet rs = null;
		List<Uscita> lista = new ArrayList<Uscita>();
		List<Articolo> listaArticoli;

		try {
			stat = connessioneDB.createStatement();
			rs = stat.executeQuery(LISTAUSCITE);
			while ( rs.next() ) {	
				String NumBolla = rs.getString("NumBolla");
				Date DataUscita = rs.getDate("DataUscita");
				String spedizioniere = rs.getString("NomeSped");
				String ordineRef = rs.getString("RifOrdine");
				String CFN = recuperaCFNegozio(ordineRef);
				listaArticoli = listaArticoliUscita(NumBolla);
				lista.add(new Uscita(NumBolla,DataUscita,spedizioniere,ordineRef,CFN,listaArticoli));
			} 
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (stat != null)
					stat.close();
			}
			catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return lista;
	}

	/**
	 * Metodo che restituisce la lista di tutti gli articoli che compongono un'uscita
	 * @param numbolla numero di bolla
	 * @return List of Articolo, lista degli articoli che compongono un'uscita
	 */
	private List<Articolo> listaArticoliUscita(String numbolla){

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Articolo> lista = new ArrayList<Articolo>();

		try {
			pstmt = connessioneDB.prepareStatement( LISTAARTICOLIUSCITA );
			pstmt.clearParameters();
			pstmt.setString( 1, numbolla );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {
				String CodiceArticolo = rs.getString("CodiceArticolo");
				double Prezzo = rs.getDouble("Prezzo");
				String DataProd = rs.getString("DataProd");
				String Posizione = rs.getString("Posizione");
				String TipoArt = rs.getString("TipoArt");
				String CodIngresso = rs.getString("CodIngresso");
				lista.add(new Articolo(CodiceArticolo,Prezzo,DataProd,Posizione,TipoArt,CodIngresso));
			} 
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}
			catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return lista;
	}

	/**
	 * Metodo che verifica se il nome del tipo articolo che si vuole aggiungere è già presente nel database
	 * @param nomeTipo nome da verificare
	 * @return ture se nome e' gia' presente
	 */
	public boolean verificaNomeTipo(String nomeTipo) {

		boolean esito = false;
		int ris = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = connessioneDB.prepareStatement( VERIFICANOMETIPOART );
			pstmt.clearParameters();
			pstmt.setString( 1, nomeTipo );
			rs = pstmt.executeQuery();
			rs.next();
			ris = rs.getInt("count");
			if (ris > 0)
				esito=true;	
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}
			catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return esito;
	}

	/**
	 * Metodo che si occupa dell'inserimento di un nuovo tipo articolo
	 * @param nome
	 * @param descr
	 * @param materiale
	 * @param sport
	 */
	public void inserimentoTipoArticolo(String nome, String descr, String materiale, String sport) {

		PreparedStatement pstat = null;
		try {
			pstat = connessioneDB.prepareStatement(INSERTTIPOART);
			pstat.setString(1, nome);
			pstat.setString(2, descr);
			pstat.setString(3, materiale);
			pstat.setString(4, sport);
			pstat.executeUpdate();
		}catch (SQLException e) {e.printStackTrace();} 
		finally {
			try {
				if (pstat != null)
					pstat.close();
			}catch (Exception e) {e.printStackTrace();}
		}	    
	}

	/**
	 * Metodo che ritorna la lista di tutti gli sport presenti nel database
	 * @return  List of String, lista di tutti gli sport
	 */
	public List<String> getListaSport(){
		Statement stat = null;
		ResultSet rs = null;
		List<String> lista = new ArrayList<String>();

		try {
			stat = connessioneDB.createStatement();
			rs = stat.executeQuery(LISTASPORT);
			while ( rs.next() ) {	
				lista.add(rs.getString("nomeSport"));
			}
		} catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (stat != null)
					stat.close();
				}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return lista;   	    	
	}

	/**
	 * Metodo che ritorna la lista di tutti gli spedizionieri presenti nel database
	 * @return List of String, lista di tutti gli spedizionieri
	 */
	public List<String> getListaSpedizionieri(){
		Statement stat = null;
		ResultSet rs = null;
		List<String> lista = new ArrayList<String>();

		try {
			stat = connessioneDB.createStatement();
			rs = stat.executeQuery(LISTASPEDIZIONIERI);
			while ( rs.next() ) {	
				lista.add(rs.getString("nomeS"));
			} 
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (stat != null)
					stat.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return lista;   	    	
	}

	/**
	 * Metodo che recupera tutti gli articoli che compongono un ingresso
	 * @param codiceIngresso codice dell'ingresso di cui recuperare la lista
	 * @return List of Articolo, lista contenente tutti gli articoli di un ingresso
	 */
	private List<Articolo> getListaArticoliIngresso(String codiceIngresso){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Articolo> lista = new ArrayList<Articolo>();

		try {
			pstmt = connessioneDB.prepareStatement( LISTAARTICOLIINGRESSO);
			pstmt.clearParameters();
			pstmt.setString( 1, codiceIngresso );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {	
				String CodiceArticolo = rs.getString("CodiceArticolo");
				double Prezzo = rs.getDouble("Prezzo");
				String DataProd = rs.getString("DataProd");
				String Posizione = rs.getString("Posizione");
				String TipoArt = rs.getString("TipoArt");
				String CodIngresso = rs.getString("CodIngresso");
				lista.add(new Articolo(CodiceArticolo,Prezzo,DataProd,Posizione,TipoArt,CodIngresso));
			} 
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return lista;
	}

	/**
	 * Metodo che ritorna  la lista di tutti i movimenti in ingresso
	 * @return List of Ingresso, lista di ingressi
	 */
	public List<Ingresso> listaIngressi(){
		Statement stat = null;
		ResultSet rs = null;
		List<Ingresso> lista = new ArrayList<Ingresso>();

		try {
			stat = connessioneDB.createStatement();
			rs = stat.executeQuery(LISTAINGRESSI);
			while ( rs.next() ) {	
				String codiceIngresso= rs.getString("codiceIngresso");
				Date dataIngresso = rs.getDate("dataIngresso");
				List<Articolo> listaArticoli = getListaArticoliIngresso(codiceIngresso);
				lista.add(new Ingresso(codiceIngresso,dataIngresso,listaArticoli));
			} 
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (stat != null)
					stat.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return lista;  	
	}

	/**
	 * Metodo che legge il prezzo di un aticolo del tipo dato
	 * @param tipo tipo articolo
	 * @return double, prezzo
	 */
	public double getPrezzoTipo(String tipo) {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		double prezzo=0.00;

		try {
			pstmt = connessioneDB.prepareStatement( PREZZOTIPOART );
			pstmt.clearParameters();
			pstmt.setString( 1, tipo );
			rs = pstmt.executeQuery();
			if (rs.next())
				prezzo = rs.getDouble("prezzo");
		} 
		catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return prezzo;  
	}

	/**
	 * Metodo che verifica la correttezza del codice fiscale del negozio inserito
	 * @param cf codice fiscale negozio
	 * @return true se e' corretto
	 */
	public boolean verificaCFNegozio(String cf) {

		boolean esito = false;
		int ris = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = connessioneDB.prepareStatement( VERIFICACFNEGOZIO );
			pstmt.clearParameters();
			pstmt.setString( 1, cf );
			rs = pstmt.executeQuery();
			rs.next();
			ris = rs.getInt("count");
			if (ris > 0)
				esito=true;	
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return esito;
	}

	/**
	 * Metodo che dato il codice fiscale di un negozio ritorna la lista delle uscite a lui destinate
	 * @param cfNegozio codice fiscale negozio
	 * @return List of Uscita, lista delle uscite per negozio
	 */
	public List<Uscita> getUscitePerNegozio(String cfNegozio){

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Uscita> lista = new ArrayList<Uscita>();
		List<Articolo> listaArticoli;

		try {Class.forName(JDBC_DRIVER);} catch (ClassNotFoundException e) {e.printStackTrace();}

		try {
			pstmt = connessioneDB.prepareStatement( LISTAUSCITENEGOZIO );
			pstmt.clearParameters();
			pstmt.setString( 1, cfNegozio );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {	
				String NumBolla = rs.getString("NumBolla");
				Date DataUscita = rs.getDate("DataUscita");
				String spedizioniere = rs.getString("NomeSped");
				String ordineRef = rs.getString("RifOrdine");
				String CFN = recuperaCFNegozio(ordineRef);
				listaArticoli = listaArticoliUscita(NumBolla);
				lista.add(new Uscita(NumBolla,DataUscita,spedizioniere,ordineRef,CFN,listaArticoli));
			} 
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return lista;
	}

	/**
	 * Metodo che si occupa di registrare nel database un ordine
	 * @param ordine ordine da registrare
	 */
	public void inserimentoOrdine (Ordine ordine) {
		PreparedStatement pstmt = null;

		try {
			pstmt = connessioneDB.prepareStatement( INSERTORDINE );
			pstmt.clearParameters();
			//inserisco uscita
			pstmt.setString( 1, ordine.getCodice());
			pstmt.setDate(2, ordine.getData());
			pstmt.setString(3, ordine.getNegozio());
			pstmt.setDouble(4, ordine.getPrezzoTotale());
			pstmt.executeUpdate();			
			
			try {
				if (pstmt != null)
					pstmt.close();}
			catch( SQLException sqle1 ) {sqle1.printStackTrace();}
			
			pstmt = connessioneDB.prepareStatement( INSERTCOMPOSTO );						
			//faccio update su articoli
			List<TipoArticoloOr> lista=ordine.getListaArticoliOrdinati();
			for (int i=0;i<lista.size();i++) {
				TipoArticoloOr tipoArticolo = lista.get(i);
				pstmt.clearParameters();
				pstmt.setString(1, ordine.getCodice());
				pstmt.setString(2, tipoArticolo.getNome());
				pstmt.setInt(3, tipoArticolo.getQuantita());
				pstmt.executeUpdate();
			}		
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (pstmt != null)
					pstmt.close();
				}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
	}

	/**
	 * Metodo che si occupa di recuperare gli ordini ai quali non corrisponde un'uscita
	 * @return List of Ordine, lista ordini non evasi
	 */
	public List<Ordine> getListaOrdiniInevasi(){

		Statement stat = null;
		ResultSet rs = null;
		List<Ordine> lista = new ArrayList<Ordine>();
		List<TipoArticoloOr> listaProd;

		try {
			stat = connessioneDB.createStatement();
			rs = stat.executeQuery(LISTAORDININONEVASI);
			while ( rs.next() ) {	
				String codOrdine = rs.getString("CodiceOrdine");
				Date dataOrdine = rs.getDate("DataOrdine");
				double prezzo = rs.getDouble("prezzoTotale");
				String codFisc = rs.getString("CFNegozio");
				listaProd = getListaArticoliOrdine(codOrdine);
				lista.add(new Ordine(codOrdine,dataOrdine,codFisc,prezzo,listaProd));
			} 
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (stat != null)
					stat.close();
				}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return lista;
	}

	/**
	 * Metodo che verifica se e' necessario aggiornare lo storico
	 */
	public void checkStorico() {
		int mesiMancanti=calcolaMesiMancantiStorico();

		if(mesiMancanti>0)
			preparaStorico(mesiMancanti);
	}

	/**
	 * Metodo che calcola il numero di mesi che non sono stati registrati nello storico
	 * @return int, numero mesi mancanti
	 */
	private int calcolaMesiMancantiStorico() {

		int nMesiMancanti=0,meseUltimo,annoUltimo;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int annoAttuale=Calendar.getInstance().get(Calendar.YEAR);
		int meseAttuale=Calendar.getInstance().get(Calendar.MONTH);


		try {
			pstmt = connessioneDB.prepareStatement( LASTAGGIORNAMENTOSTORICO );
			rs = pstmt.executeQuery();
			// se rs vuoto perche' non c'e' nulla nella tabella storico
			if(rs.next()) {	
				annoUltimo = rs.getInt("anno");
				meseUltimo = rs.getInt("meseUltimo");
				if(annoUltimo < Calendar.getInstance().get(Calendar.YEAR))
					//non sottraggo 1 a getMonth perche' ritorna valori 0-11
					nMesiMancanti=(12*(annoAttuale-annoUltimo)-meseUltimo)+meseAttuale;
				else
					//non sottraggo 1 a getMonth perche' ritorna valori 0-11
					nMesiMancanti= meseAttuale-meseUltimo;
			}
			else 
				return 1;
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return nMesiMancanti;
	}

	/**
	 * Metodo che chiama le funzioni per calcolare le entrate e uscite per goni tipo e ne registra i
	 * valori nella tabella storico
	 */
	private void preparaStorico(int mesiM) {
		
		List<String> listaTipi = getStringListaTipi();
		int ent = 0;
		int usc = 0;
		int mesiMancanti=mesiM;
				
		for(int mesi=mesiMancanti;mesi>0;mesi--) {			
			for (int i=0;i<listaTipi.size();i++) {				
				ent = calcoloEntrate(listaTipi.get(i),mesi);			
				usc = calcoloUscite(listaTipi.get(i),mesi);
				inserimentoStorico(listaTipi.get(i), ent, usc,mesi);
			}
		}
	}

	/**
	 * Metodo che calcola quanti articoli, di un tipo dato, sono entrati in un dato mese
	 * @param tipo tipo articolo
	 * @param mese mese di riferimento
	 * @return int, numero articoli entrati
	 */
	private int calcoloEntrate(String tipo,int mese) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = 0;
		try {
			pstmt = connessioneDB.prepareStatement( GETENTRATETIPOSTORICO );
			pstmt.clearParameters();
			pstmt.setString( 1, tipo );
			pstmt.setInt( 2, mese );
			pstmt.setInt( 3, mese );
			rs = pstmt.executeQuery();
			rs.next();
			result = rs.getInt("count");
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return result;
	}

	/**
	 * Metodo che calcola quanti articoli, di un tipo dato, sono uscita in un dato mese
	 * @param tipo tipo articolo
	 * @param mese mese di riferimento
	 * @return int, numero articoli usciti
	 */
	private int calcoloUscite(String tipo,int mese) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = 0;

		try {
			pstmt = connessioneDB.prepareStatement( GETUSCITETIPOSTORICO);
			pstmt.clearParameters();
			pstmt.setString( 1, tipo );
			pstmt.setInt( 2, mese );
			pstmt.setInt( 3, mese );

			rs = pstmt.executeQuery();
			rs.next();
			result = rs.getInt("count");
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return result;
	}

	/**
	 * Metodo che si occupa dell'effettivo inserimento delle voci dello storico all'interno del database
	 * @param tipo
	 * @param entrate
	 * @param uscite
	 * @param mese
	 */
	private void inserimentoStorico(String tipo, int entrate, int uscite,int mese) {
		PreparedStatement pstat = null;
		try {
			pstat = connessioneDB.prepareStatement(INSERTSTORICO);
			pstat.setString(1, tipo);
			pstat.setInt(2, mese);
			pstat.setInt(3, mese);
			pstat.setInt(4, entrate);
			pstat.setInt(5, uscite);
			pstat.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if (pstat != null)
					pstat.close();
			}catch (Exception e) {e.printStackTrace();}
		}	
	}

	/**
	 * Metodo che ritorna una lista di tutti fli articoli presenti in magazzino che fanno parte dei tipi
	 * prensenti nell'ordine selezionato
	 * @param rifOrdine ordine di riferimento
	 * @return List of Articolo , lista di articoli
	 */
	public List<Articolo> getArticoliUscenti(String rifOrdine){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Articolo> lista = new ArrayList<Articolo>();

		try {
			pstmt = connessioneDB.prepareStatement( LISTAARTICOLIEVADIBILI );
			pstmt.clearParameters();
			pstmt.setString( 1, rifOrdine );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {	
				String CodiceArticolo = rs.getString("CodiceArticolo");
				double Prezzo = rs.getDouble("Prezzo");
				String DataProd = rs.getString("DataProd");
				String Posizione = rs.getString("Posizione");
				String TipoArt = rs.getString("TipoArt");
				String CodIngresso = rs.getString("CodIngresso");
				lista.add(new Articolo(CodiceArticolo,Prezzo,DataProd,Posizione,TipoArt,CodIngresso));
			} 
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return lista;
	}

	/**
	 * Metodo che calcola quanti articoli di un dato tipo, sno presenti in magazzino
	 * @param nometipo tipo articolo 
	 * @return int, numero articoli per tipo
	 */
	private int getGiacenzeMagazzino(String nometipo) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int numero = -10;

		try {
			pstmt = connessioneDB.prepareStatement( VERIFICAGIACENZA );
			pstmt.clearParameters();
			pstmt.setString( 1, nometipo );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {	
				numero = rs.getInt("COUNT");
			} 
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return numero;
	}

/**
 * Metodo che verifica la disponibilita' di articoli rispetto ad un ordine dato
 * @param rifOrdine ordine di riferimento
 * @return true se ci sono abbastanza articoli per evadere l'ordine
 */
	public boolean verificaGiacenzePerOrdine(String rifOrdine) {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean esito = false;

		try {
			pstmt = connessioneDB.prepareStatement( LISTATIPIORDINE );
			pstmt.clearParameters();
			pstmt.setString( 1, rifOrdine );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {	
				String nometipo = rs.getString("NomeT");
				int quan = rs.getInt("Quantita");
				int giac = getGiacenzeMagazzino(nometipo);
				if ( quan<=giac)
					esito=true;
				else
					return false;
			} 
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return esito;
	}
	
	/**
	 * Metodo che recupara la lista di codici fiscali di tutti i negozi
	 * @return List of String, lista codici fiscali negozi
	 */
	public List<String> getListaCFNegozi() {
		Statement stat = null;
		ResultSet rs = null;
		List<String> lista = new ArrayList<String>();

		try {
			stat = connessioneDB.createStatement();
			rs = stat.executeQuery(LISTACFNEGOZI);
			while ( rs.next() ) {	
				lista.add(rs.getString("CF"));
			} 
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {
				if (stat != null)
					stat.close();
			}catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		return lista;
	}
}

