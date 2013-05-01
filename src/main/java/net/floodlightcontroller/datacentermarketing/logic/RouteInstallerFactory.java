package net.floodlightcontroller.datacentermarketing.logic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This factory creates timers for winning bids
 * to install routes 
 * It creates timers to be triggered when the start time of winning bids comes
 * 
 * Another thing when the installer trigger wakes is to inform the client that 
 * he could start communication now (so act as a reminder for clients) * 
 * @author shu
 *
 */

public class RouteInstallerFactory {
	private static RouteInstallerFactory _instance = null;

	public static RouteInstallerFactory getInstance() {
		if (_instance == null)
			_instance = new RouteInstallerFactory();
		return _instance;
	}
	
	/**
	 * The trigger function
	 */
	
	ActionListener routeInstallTriggered = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			
		}
	};
	
	
}
