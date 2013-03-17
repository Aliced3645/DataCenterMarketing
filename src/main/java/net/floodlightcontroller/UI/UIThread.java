package net.floodlightcontroller.UI;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

//thread for swing GUI
public class UIThread implements Runnable{
	
	Thread uiThread;
	static FlowUI flowUI;
	
	public static FlowUI getFlowUI(){
		return UIThread.flowUI;
	}
	
	
	public UIThread(){
		uiThread = new Thread(this, "UI");
		uiThread.start();
	}

	private static void createUI(){
		flowUI = new FlowUI();
		flowUI.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		flowUI.setVisible(true);
	}
	
	@Override
	public void run() {
		System.out.println("UIThread starting");
		UIThread.createUI();
		System.out.println("UIThread started");
	}
}
