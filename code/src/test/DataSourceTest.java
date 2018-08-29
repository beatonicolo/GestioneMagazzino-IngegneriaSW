package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.jupiter.api.Test;

import model.dbcom.DataSource;
import model.entities.Articolo;
import model.entities.Ingresso;
import model.entities.Negozio;
import model.entities.Ordine;
import model.entities.TipoArticolo;
import model.entities.TipoArticoloOr;
import model.entities.Uscita;

class DataSourceTest {

	private static String JDBC_DRIVER = "org.postgresql.Driver";  
	private static String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
	private static String USER = "postgres";
	private static String PASS = "password";
	private static String lastOrder="SELECT CodiceOrdine FROM ordine ORDER BY CodiceOrdine::int DESC";
	private static String lastIngr="SELECT CodiceIngresso FROM ingresso ORDER BY CodiceIngresso::int DESC";
	private static String nextUscita="SELECT MAX(date_part('year', DataUscita)), MAX(NumBolla) FROM uscita WHERE date_part('year',CURRENT_DATE) = date_part('year', DataUscita)";
	private static String delOrdine="DELETE FROM ordine WHERE CodiceOrdine = ?";
	private static String delIngresso="DELETE FROM ingresso WHERE CodiceIngresso = ?";
	private static String delUscita="DELETE FROM uscita WHERE NumBolla = ?";
	private static String delTipo="DELETE FROM TipoArticolo WHERE NomeTipo = ?";
	private static String leggiStorico="SELECT mese,entrate FROM storico WHERE TipoA='cuffia'";
	private static String ripristinaStorico="DELETE FROM storico;"
			+ "INSERT INTO storico (TipoA, Mese, Anno, Entrate, Uscite) VALUES ('pallone in cuoio',1,2018,'0','0'),\n" + 
			"('pallone leggero',1,2018,'0','0'),\n" + 
			"('racchetta professionale',1,2018,'0','0'),\n" + 
			"('cuffia',1,2018,'0','0'),\n" + 
			"('occhialini',1,2018,'0','0'),\n" + 
			"('canottiera cleveland',1,2018,'0','0'),\n" + 
			"('kit golf principiante',1,2018,'0','0'),\n" + 
			"('scarpe mercurial',1,2018,'0','0'),\n" + 
			"('racchettoni',1,2018,'0','0'),\n" + 
			"('ciabatte uomo nere',1,2018,'0','0'),\n" + 
			"('casco sportivo',1,2018,'0','0'),\n" + 
			"('guanti palestra',1,2018,'0','0'),\n" + 
			"('casco rugby',1,2018,'0','0'),\n" + 
			"('borraccia ciclismo',1,2018,'0','0'),\n" + 
			"('scarponi unisex alpinismo',1,2018,'0','0');";
	private static String ripristinaStorico1="DELETE FROM storico;";
	private static String ripristinaStorico2="INSERT INTO storico (TipoA, Mese, Anno, Entrate, Uscite) VALUES ('pallone in cuoio',1,2018,'0','0'),\n" + 
			"('pallone leggero',1,2018,'0','0'),\n" + 
			"('racchetta professionale',1,2018,'0','0'),\n" + 
			"('cuffia',1,2018,'0','0'),\n" + 
			"('occhialini',1,2018,'0','0'),\n" + 
			"('canottiera cleveland',1,2018,'0','0'),\n" + 
			"('kit golf principiante',1,2018,'0','0'),\n" + 
			"('scarpe mercurial',1,2018,'0','0'),\n" + 
			"('racchettoni',1,2018,'0','0'),\n" + 
			"('ciabatte uomo nere',1,2018,'0','0'),\n" + 
			"('casco sportivo',1,2018,'0','0'),\n" + 
			"('guanti palestra',1,2018,'0','0'),\n" + 
			"('casco rugby',1,2018,'0','0'),\n" + 
			"('borraccia ciclismo',1,2018,'0','0'),\n" + 
			"('scarponi unisex alpinismo',1,2018,'0','0');";

	private DataSource ds = DataSource.getInstance();
	@Test
	void testModPosizione() {
		//Legge la posizione attuale, la cambia, verifica e rimette la posizione di partenza
		Articolo orig=ds.getArticolo("2");
		assertEquals(orig.getPosizione(), "a-1");
		ds.modPosizione("a-33", "2");
		Articolo nuovo=ds.getArticolo("2");
		assertEquals(nuovo.getPosizione(), "a-33");
		ds.modPosizione(orig.getPosizione(), "2");
	}

	@Test
	void testNextOrdine() {
		String result="";
		int n=0;
		ResultSet rs = null;
		Connection con = null;
		Statement stat = null;	
		try {
			con = DriverManager.getConnection( DB_URL, USER,PASS);
			stat = con.createStatement();
			rs = stat.executeQuery(lastOrder);
			rs.next();
			result = rs.getString("CodiceOrdine");
		}catch (SQLException e) {e.printStackTrace();}
		finally {
			try {
				if (stat != null) 
					stat.close();
				if (con != null)
					con.close();
			}catch (Exception e) {e.printStackTrace();}
		}	
		n = Integer.parseInt(result);
		assertEquals(ds.nextOrdine(),String.valueOf(++n));	
	}

	@Test
	void testNextIngresso() {
		String result="";
		int n=0;
		ResultSet rs = null;
		Connection con = null;
		Statement stat = null;	
		try {
			con = DriverManager.getConnection( DB_URL, USER,PASS);
			stat = con.createStatement();
			rs = stat.executeQuery(lastIngr);
			rs.next();
			result = rs.getString("CodiceIngresso");
		}catch (SQLException e) {e.printStackTrace();}
		finally {
			try {
				if (stat != null) 
					stat.close();
				if (con != null)
					con.close();
			}catch (Exception e) {e.printStackTrace();}
		}	
		n = Integer.parseInt(result);
		assertEquals(ds.nextIngresso(),String.valueOf(++n));	
		}
	
	@Test
	void testNextUscita() {
		ResultSet rs = null;
		Connection con = null;
		Statement stat = null;
		String anno="";
		String numbolla="";
		int dat=0;
		int num=0;
		int year = Calendar.getInstance().get(Calendar.YEAR);

		try {Class.forName(JDBC_DRIVER);} catch (ClassNotFoundException e) {e.printStackTrace();}
		try {
			con = DriverManager.getConnection( DB_URL, USER,PASS);
			stat = con.createStatement();
			rs = stat.executeQuery(nextUscita);
			rs.next();
			if ( rs.getString(1) != null ) {
				anno = rs.getString(1);
				numbolla = rs.getString(2); }
			else {
				num=1; }
		}catch (SQLException e) {e.printStackTrace();}
		finally {
			try {
				if (stat != null)stat.close();
				if (con != null)
					con.close();
			}catch (Exception e) {e.printStackTrace();}
		}	
		dat = Integer.parseInt(anno);
		num = Integer.parseInt(numbolla);
		if (dat == year)
			num++;
		else 
			num=1;		
		assertEquals(ds.nextUscita(),String.valueOf(num));	
	}
	
 	@Test
	void testVerificaArticolo() {
 		//Leggo un oggetto articolo partendo dal codice
		Articolo art1 = ds.getArticolo("1");
		String dataArt1 = art1.getDataProduzione();
		assertEquals(dataArt1, "2017-03-10");
		Articolo art5 = ds.getArticolo("5");
		String dataArt5 = art5.getDataProduzione();
		assertEquals(dataArt5, "2017-02-21");
	}

	@Test
	void testGetArticolo() {
		//Leggo un oggetto articolo passandogli il codice
		Articolo test=ds.getArticolo("2");
		assertEquals(test.getPosizione(), "a-1");
		Articolo test2=ds.getArticolo("5");
		assertEquals(test2.getPosizione(), "a-2");
	}
	
	@Test
	void testRecuperaCFNegozio() {
		//Passandogli il numero ordine, recupera il cf del negozio corrispondente
		assertEquals(ds.recuperaCFNegozio("1"), "lzzmnl");
		assertEquals(ds.recuperaCFNegozio("3"), "beatto");
		assertEquals(ds.recuperaCFNegozio("5"), "nclbts");
	}
		
	@Test
	
	void testLoginMagazziniere() {
 		assertEquals(ds.loginMagazziniere("lazza", "lazza"), true);
 		assertEquals(ds.loginMagazziniere("lazza", "nonlazza"), false);
	}
		
	@Test
	void testLoginNegozio() {
 		assertEquals(ds.loginNegozio("lazza", "lazza"), true);
 		assertEquals(ds.loginNegozio("lazza", "nonlazza"), false);
	}
	
	@Test
	void testLoginSegreteria() {
 		assertEquals(ds.loginSegreteria("lazza", "lazza"), true);
 		assertEquals(ds.loginSegreteria("lazza", "nonlazza"), false);
	}
	
	@Test
	void testGetListaSport() {
		List<String> list = ds.getListaSport();
		assertEquals(list.contains("calcio"), true);
		assertEquals(list.contains("golf"), true);
		assertEquals(list.contains("nullo"), false);
	}

	@Test
	void testGetListaSpedizionieri() {
		List<String> list = ds.getListaSpedizionieri();
		assertEquals(list.contains("dhl"), true);
		assertEquals(list.contains("bartolini"), true);
		assertEquals(list.contains("nullo"), false);
	}
	
	@Test
	void testListaIngressi() {
		//Leggo la lista degli ingressi dal db
		List<Ingresso> list = ds.listaIngressi();
		String codice1 = list.get(0).getCodice();
		assertEquals(codice1, "1");
		String codice2 = list.get(3).getCodice();
		assertEquals(codice2, "4");
	}
	
	@Test
	void testGetListaTipi() {
		List<TipoArticolo> lista = ds.getListaTipi();
		String nome1 = lista.get(0).getNome();
		assertEquals(nome1, "pallone in cuoio");
		String nome2 = lista.get(7).getNome();
		assertEquals(nome2, "scarpe mercurial");
	}
	
	@Test
	void testGetNegozio() {
		//Ottengo l'oggetto negozio passandogli l'username
		Negozio test1=ds.getNegozio("lazza");
		assertEquals(test1.getCodFisc() , "lzzmnl");
		Negozio test2=ds.getNegozio("deguio");
		assertEquals(test2.getCodFisc() , "dgiflo");
	}
	
	@Test
	void testVerificaCFNegozio() {
		//Controllo se il cf é presente fra i nostri negozi
		assertEquals(ds.verificaCFNegozio("lzzmnl"), true);
		assertEquals(ds.verificaCFNegozio("flirro"), true);
		assertEquals(ds.verificaCFNegozio("nullo"), false);
	}
	
	@Test
	void testVerificaNomeTipo() {
		//Verifico la presenza del tipo articolo
		assertEquals(ds.verificaNomeTipo("racchettoni"), true);
		assertEquals(ds.verificaNomeTipo("cuffia"), true);
		assertEquals(ds.verificaNomeTipo("nullo"), false);
	}
	
	@Test
	void testGetListaOrdiniNegozio() {
		//Ottengo la lista degli ordini di un negozio e verifico che sia corretta
		List<Ordine> lista = ds.getListaOrdiniNegozio("lzzmnl");
		assertEquals(lista.get(0).getNegozio(),"lzzmnl");
		assertEquals(lista.get(1).getNegozio(),"lzzmnl");
		List<Ordine> lista2 = ds.getListaOrdiniNegozio("beatto");
		assertEquals(lista2.get(0).getNegozio(),"beatto");
		assertEquals(lista2.get(1).getNegozio(),"beatto");
	}

	@Test
	void testInserimentoOrdine() {
		
		List<TipoArticoloOr> listaOr = new ArrayList<TipoArticoloOr>();
		int num = Integer.parseInt(ds.nextOrdine()); //Salvo il numero del prossimo ordine
		TipoArticoloOr newTipo = new TipoArticoloOr("cuffia", "descrizione", "materiale", "nuoto", 44); //Creo un tipoarticolo di prova
		listaOr.add(newTipo);
		//Creo un ordine per il test
		Ordine test = new Ordine(String.valueOf(num), java.sql.Date.valueOf("2018-06-06"), "lzzmnl", 1199, listaOr);
		//Inserisco il nuovo ordine e verifico se é stato inserito correttamente
		ds.inserimentoOrdine(test);
		List<Ordine> lista = ds.getListaOrdiniNegozio("lzzmnl"); //Salvo la lista degli ordini del negozio
		String lastorder = lista.get(lista.size()-2).getCodice(); //Salvo il codice dell'ordine precedente a quello di test
		assertEquals(lista.get(lista.size()-1).getCodice(), String.valueOf(num));
		assertEquals(lista.get(lista.size()-1).getPrezzoTotale(), 1199);
		//Rimuovo l'ordine di test
		Connection con = null;
		PreparedStatement pstat = null;
		try {Class.forName(JDBC_DRIVER);} catch (ClassNotFoundException e) {e.printStackTrace();}
		try {
			con = DriverManager.getConnection( DB_URL, USER,PASS);
			pstat = con.prepareStatement(delOrdine);
			pstat.setString(1, String.valueOf(num));
			pstat.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if (pstat != null)
					pstat.close();
				if (con != null)
					con.close();
			}catch (Exception e) {e.printStackTrace();}
		}
		//Verifico di aver rimosso l'ordine di test
		lista = ds.getListaOrdiniNegozio("lzzmnl"); //Leggo la lista degli ordini aggiornata
		assertEquals(lastorder, lista.get(lista.size()-1).getCodice());
	}
	
	@Test
	void testInserimentoIngresso() {
		int num = Integer.parseInt(ds.nextIngresso()); //Salvo il numero del prossimo ingresso
		List<Articolo> listaArt = new ArrayList<Articolo>(); 
		listaArt.add(new Articolo("99999", 66, "2010-10-10", "C-11", "cuffia", String.valueOf(num))); //Articolo di test da inserire
		Ingresso nuovoIngr=new Ingresso(String.valueOf(num),java.sql.Date.valueOf("2018-06-06"),listaArt); //Creo un ingresso per il test
		//Inserisco il nuovo ingresso e verifico se é stato inserito correttamente
		ds.inserimentoIngresso(nuovoIngr);
		List<Ingresso> lista = ds.listaIngressi(); //Salvo la lista degli ingressi
		String lastIng = lista.get(lista.size()-2).getCodice(); //Salvo il codice dell'ingresso precedente a quello di test
		assertEquals(lista.get(lista.size()-1).getCodice(), String.valueOf(num));
		assertEquals(lista.get(lista.size()-1).getData(), java.sql.Date.valueOf("2018-06-06"));
		//Rimuovo l'ingresso di test
		Connection con = null;
		PreparedStatement pstat = null;
		try {Class.forName(JDBC_DRIVER);} catch (ClassNotFoundException e) {e.printStackTrace();}
		try {
			con = DriverManager.getConnection( DB_URL, USER,PASS);
			pstat = con.prepareStatement(delIngresso);
			pstat.setString(1, String.valueOf(num));
			pstat.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if (pstat != null)
					pstat.close();
				if (con != null)
					con.close();
			}catch (Exception e) {e.printStackTrace();}
		}
		//Verifico di aver rimosso l'ingresso di test
		lista = ds.listaIngressi(); //Leggo la lista degli ingressi aggiornata
		assertEquals(lastIng, lista.get(lista.size()-1).getCodice());	
	}

	@Test
	void testInserimentoUscita() {
		int num = Integer.parseInt(ds.nextUscita()); //Salvo il numero della prossima uscita
		List<Articolo> listaArt = new ArrayList<Articolo>(); 
		listaArt.add(new Articolo("99999", 66, "2010-10-10", "C-11", "cuffia", String.valueOf(num))); //Articolo di test da inserire
		Uscita nuovaUsc=new Uscita(String.valueOf(num),java.sql.Date.valueOf("2018-06-06"),"dhl", "1", "lzzmnl", listaArt); //Creo un'uscita per il test
		//Inserisco la nuova uscita e verifico se é stata inserita correttamente
		ds.inserimentoUscita(nuovaUsc);
		List<Uscita> lista = ds.getListaUscite(); //Salvo la lista delle uscite
		String lastUsc = lista.get(lista.size()-2).getBolla(); //Salvo il codice dell'uscita precedente a quella di test
		assertEquals(lista.get(lista.size()-1).getBolla(), String.valueOf(num));
		assertEquals(lista.get(lista.size()-1).getData(), java.sql.Date.valueOf("2018-06-06"));
		//Rimuovo l'uscita di test
		Connection con = null;
		PreparedStatement pstat = null;
		try {Class.forName(JDBC_DRIVER);} catch (ClassNotFoundException e) {e.printStackTrace();}
		try {
			con = DriverManager.getConnection( DB_URL, USER,PASS);
			pstat = con.prepareStatement(delUscita);
			pstat.setString(1, String.valueOf(num));
			pstat.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if (pstat != null)
					pstat.close();
				if (con != null)
					con.close();
			}catch (Exception e) {e.printStackTrace();}
		}
		//Verifico di aver rimosso l'uscita di test
		lista = ds.getListaUscite(); //Leggo la lista delle uscite aggiornata
		assertEquals(lastUsc, lista.get(lista.size()-1).getBolla());
	}

	@Test
	void testInserimentoNomeTipo() {
		ds.inserimentoTipoArticolo("nometiponullo", "descrizione", "materiale", "nuoto"); //Inserisco il nome tipo per il test
		List <TipoArticolo> lista = ds.getListaTipi(); //Leggo la lista dei tipi articolo
		String lastTipo = lista.get(lista.size()-2).getNome(); //Salvo il nome del tipoarticolo precedente a quello di test
		assertEquals(lista.get(lista.size()-1).getNome(), "nometiponullo"); //Verifico l'inserimento
		assertEquals(lista.get(lista.size()-1).getMateriali(), "materiale");
		//Rimuovo il nome tipo di test
		Connection con = null;
		PreparedStatement pstat = null;
		try {Class.forName(JDBC_DRIVER);} catch (ClassNotFoundException e) {e.printStackTrace();}
		try {
			con = DriverManager.getConnection( DB_URL, USER,PASS);
			pstat = con.prepareStatement(delTipo);
			pstat.setString(1, String.valueOf("nometiponullo"));
			pstat.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if (pstat != null)
					pstat.close();
				if (con != null)
					con.close();
			}catch (Exception e) {e.printStackTrace();}
		}	
		// Verifico di aver rimosso il nome tipo di test
		lista = ds.getListaTipi();
		assertEquals(lista.get(lista.size()-1).getNome(), lastTipo);
	}
	
	@Test
	void testGetListaUscite() {
		List<Uscita> lista = ds.getListaUscite(); //Salvo la lista delle uscite
		String codice1 = lista.get(0).getBolla();
		assertEquals(codice1, "1");
		String codice2 = lista.get(3).getBolla();
		assertEquals(codice2, "4");
		String sped = lista.get(3).getSpedizioniere();
		assertEquals(sped, "dhl");
	}
	
	@Test
	void testGetPrezzoTipo() {
		//Leggo il prezzo di un tipo articolo e verifico la correttezza
		double prezzo1 = ds.getPrezzoTipo("cuffia");
		assertEquals(prezzo1, 14.99);
		double prezzo2 = ds.getPrezzoTipo("racchettoni");
		assertEquals(prezzo2, 12.00);	
	}

	@Test
	void testGetUscitePerNegozio() {
		//Verifico che passandogli il cf di un negozio mi ritorni la lista delle uscite associate
		List<Uscita> lista1 = ds.getUscitePerNegozio("lzzmnl");
		assertEquals(lista1.get(0).getNegozio(), "lzzmnl");
		List<Uscita> lista2 = ds.getUscitePerNegozio("beatto");
		assertEquals(lista2.get(0).getNegozio(), "beatto");
	}

	@Test
	void testGetOrdineDaUscita() {
		//Verifico che passandogli l'identificativo dell'uscita mi ritorni l'ordine corrispondente
		Ordine test1 = ds.getOrdineDaUscita("2", java.sql.Date.valueOf("2018-03-21"));
		assertEquals(test1.getCodice(), "1");
		assertEquals(test1.getNegozio(), "lzzmnl");
		Ordine test2 = ds.getOrdineDaUscita("3", java.sql.Date.valueOf("2018-03-26"));
		assertEquals(test2.getCodice(), "3");
		assertEquals(test2.getNegozio(), "beatto");
	}
	
	@Test
	void testGetListaOrdiniInevasi() {
		List<Ordine> lista = ds.getListaOrdiniInevasi();
		assertEquals( lista.get(0).getCodice(), "8");
		assertEquals( lista.get(0).getNegozio(), "flirro");
		assertEquals( lista.get(1).getCodice(), "9");
		assertEquals( lista.get(1).getNegozio(), "lzzmnl");
	}
	
	@Test
	void testGetArticoliUscenti() {
		//Verifico che passando un'ordine mi restituisca gli articoli che possono soddisfarlo e che sono presenti in magazzino 
		List<Articolo> lista = ds.getArticoliUscenti("10");
		assertEquals(lista.get(0).getPrezzo(), 39.00);
		assertEquals(lista.get(0).getCodice(), "23");
		assertEquals(lista.get(1).getPrezzo(), 39.00);
		assertEquals(lista.get(1).getCodice(), "24");
		assertEquals(lista.get(2).getPrezzo(), 59.00);
		assertEquals(lista.get(2).getCodice(), "38");
	}
	
	@Test
	void testVerificaGiacenzePerOrdine() {
		//Metodo che verifica se son presenti sufficienti articoli per evadere un ordine
		assertEquals(ds.verificaGiacenzePerOrdine("8"), false);
		assertEquals(ds.verificaGiacenzePerOrdine("9"), false);
		assertEquals(ds.verificaGiacenzePerOrdine("10"), true);
	}
	
	@Test
	void testCheckStorico() {
		//Eseguo checkStorico e verifico che abbia sistemato lo storico
		ds.checkStorico();
		
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		List <String> lista = new ArrayList <String>();
		try {Class.forName(JDBC_DRIVER);} catch (ClassNotFoundException e) {e.printStackTrace();}
		try {
			con = DriverManager.getConnection( DB_URL, USER,PASS);
			stmt = con.createStatement();
			rs = stmt.executeQuery( leggiStorico );
			while ( rs.next() ) {
				String entrate = rs.getString("entrate");
				lista.add(entrate);
			}
		}catch( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try {con.close();}
			catch( SQLException sqle1 ) {sqle1.printStackTrace();}
		}
		
		assertEquals(lista.get(0), "0");
		assertEquals(lista.get(2), "6");
		assertEquals(lista.get(3), "0");
		
	}
	
	@Test
	void testGetListaCFNegozi() {
		List <String> lista = ds.getListaCFNegozi();
		assertEquals(lista.get(0), "lzzmnl");
		assertEquals(lista.get(1), "beatto");
	}
	
	@After
	
	public final void ripristinaStorico() {
	//Ripristino lo stato iniziale della tabella storico
	Statement stmt2 = null;
	Connection con2 = null;
	try {Class.forName(JDBC_DRIVER);} catch (ClassNotFoundException e) {e.printStackTrace();}
	try {
		con2 = DriverManager.getConnection( DB_URL, USER,PASS);
		stmt2 = con2.createStatement();
		stmt2.executeUpdate(ripristinaStorico1);
	}catch( SQLException sqle ) {sqle.printStackTrace();}
	finally {
		try {con2.close();}
		catch( SQLException sqle1 ) {sqle1.printStackTrace();}
	}
	
	//Ripristino lo stato iniziale della tabella storico
	Statement stmt3 = null;
	Connection con3 = null;
	try {Class.forName(JDBC_DRIVER);} catch (ClassNotFoundException e) {e.printStackTrace();}
	try {
		con3 = DriverManager.getConnection( DB_URL, USER,PASS);
		stmt3 = con3.createStatement();
		stmt3.executeUpdate(ripristinaStorico2);
	}catch( SQLException sqle ) {sqle.printStackTrace();}
		try {con3.close();}
		catch( SQLException sqle1 ) {sqle1.printStackTrace();}
	}
	
}
