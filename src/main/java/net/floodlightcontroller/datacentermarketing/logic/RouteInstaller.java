package net.floodlightcontroller.datacentermarketing.logic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.Timer;

import net.floodlightcontroller.datacentermarketing.MarketManager;
import net.floodlightcontroller.routing.Route;


/**
 * The class is created by RouteInstallerFactory
 * @author shu
 *
 */
public class RouteInstaller {
	
	long triggerTime;
	long srcID;
	long destID;
	Route rt;
	long bandwidth;
	short timeout;
	
	
	public RouteInstaller(long triggerTime, long srcID, long destID, Route rt,
			long bandwidth, short timeout) {
		super();
		this.triggerTime = triggerTime;
		this.srcID = srcID;
		this.destID = destID;
		this.rt = rt;
		this.bandwidth = bandwidth;
		this.timeout = timeout;
		
		//create the timer
		long timeToWake = triggerTime - System.currentTimeMillis();
		Timer tickTimer = new Timer((int) timeToWake, routeInstallTriggered);
		tickTimer.setRepeats(false);
		tickTimer.start();
	}

	/**
	 * The trigger function, install route
	 */
	ActionListener routeInstallTriggered = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				MarketManager.getInstance().getLowLevelController().installRoute(srcID, destID, rt, bandwidth, timeout);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	};
	
	
	
}
