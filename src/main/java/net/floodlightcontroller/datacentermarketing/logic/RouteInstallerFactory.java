package net.floodlightcontroller.datacentermarketing.logic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.floodlightcontroller.routing.Route;

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
	 * 
	 * @param triggerTime is the absolute system when the route should be installed ( in milliseconds )
	 * @param srcID
	 * @param destID
	 * @param rt
	 * @param bandwidth
	 * @param timeout
	 */
	
	public void createRouteInstaller(long triggerTime, long srcID, long destID, Route rt,
			long bandwidth, short timeout){
		RouteInstaller routeInstaller = new RouteInstaller(triggerTime, srcID, destID, rt, bandwidth, timeout);
		return;
	}	
}
