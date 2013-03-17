package net.floodlightcontroller.UI;

import javax.swing.JFrame;
import javax.swing.JLabel;

//thread for swing GUI
public class UIThread implements Runnable{
	
	Thread uiThread;
	
	public UIThread(){
		uiThread = new Thread(this, "UI");
		uiThread.start();
	}

	private static void createUI(){
		JFrame frame = new JFrame("Flow GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel label = new JLabel("Hello World");
        frame.getContentPane().add(label);
        
        frame.pack();
        frame.setVisible(true);
	}
	
	@Override
	public void run() {
		System.out.println("UIThread starting");
		createUI();
		System.out.println("UIThread started");
	}
}
