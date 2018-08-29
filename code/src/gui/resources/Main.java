package gui.resources;

import controller.GlobalListener;
import gui.pages.Home;

public class Main {

	public static void main(String[] args) {
		MyFrame frame = new MyFrame();
	
		frame.setContentPane(new Home(new GlobalListener(frame)));
		frame.validate();
	}
	
}
