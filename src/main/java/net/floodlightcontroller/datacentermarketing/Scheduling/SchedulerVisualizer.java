/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

import javax.swing.JDialog;

/**
 * @author mininet
 * 
 */
public class SchedulerVisualizer implements Runnable {

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
	// TODO Auto-generated method stub

	SchedulerUI ui = new SchedulerUI();

	ui.setTitle("Scheduling UI");
	ui.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	ui.setVisible(true);
	System.out.println("\n\n\t\t\t111111");

	while (true) {
	    try {
		Thread.sleep(4000);
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    ui.doRepaint();

	}

    }

}
