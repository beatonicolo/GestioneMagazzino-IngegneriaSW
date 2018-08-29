package gui.resources;

import java.awt.SystemColor;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * Classe che estende e ridefinisce JFrame
 * @author beato
 *
 */
public class MyFrame extends JFrame {

	private static final int SCREENWIDTH= (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private static final int SCREENHEIGHT= (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	private final static int WIDTH = (SCREENWIDTH*8)/10;
	private final static int HEIGHT = (SCREENHEIGHT*8)/10;

	/**
	 * Costruttore del frame. Imposta lookAndFeel perchè sia coerente su tutte le piattaforme, 
	 * il comportamento all'uscita, dimensioni della finestra e la rende visibile.
	 */
	public MyFrame() {
		setTitle("GESTIONE MAGAZZINO v1.0");

		//setto il look and feel in modo che la grafica sia consistente su tutte le piattaforme
		try {UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );}
		catch (Exception e) {e.printStackTrace();}

		setBackground(SystemColor.text);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setto le dimensioni della finestra
		this.setSize(WIDTH, HEIGHT);

		this.setVisible(true);
	}
}