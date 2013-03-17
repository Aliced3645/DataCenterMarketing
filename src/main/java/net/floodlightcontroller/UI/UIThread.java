package net.floodlightcontroller.UI;

import javax.swing.JDialog;
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
		FlowUI dialog = new FlowUI();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}
	
	@Override
	public void run() {
		System.out.println("UIThread starting");
		createUI();
		System.out.println("UIThread started");
	}
}
