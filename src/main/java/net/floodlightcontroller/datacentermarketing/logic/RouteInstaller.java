package net.floodlightcontroller.datacentermarketing.logic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.Timer;

import com.cedarsoftware.util.io.JsonWriter;

import net.floodlightcontroller.datacentermarketing.MarketManager;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.routing.Route;


class TransmissionReminder{
	public String destIP;
	public long bandwidth;
	public long duration;//in millisecond
	public long data;//how much data to transmit
	
	public String toJSONString() throws IOException{
		String res = JsonWriter.objectToJson(this);
		return res;
	}
	
	public TransmissionReminder(String destIP, long bandwidth, long duration,long data ){
		this.destIP = destIP;
		this.bandwidth = bandwidth;
		this.duration = duration;
		this.data = data;
	}
}

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
	long data;
	
	private String makeTransmissionReminderString(){
		//JsonWriter.JsonStringWriter
		return null;
	}
	
	public RouteInstaller(long triggerTime, long srcID, long destID, Route rt,
			long bandwidth, short timeout, long data) {
		super();
		this.triggerTime = triggerTime;
		this.srcID = srcID;
		this.destID = destID;
		this.rt = rt;
		this.bandwidth = bandwidth;
		this.timeout = timeout;
		this.data = data;
		
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
				MarketManager.getInstance().getLowLevelController().installRoute(srcID, destID, rt, bandwidth, (short)(timeout/1000));
				//tell client that they CAN GO
				int addressInt = MarketManager.getInstance().getLowLevelController().getDevices().get(destID).getIPv4Addresses()[0];
				
				TransmissionReminder reminder = new TransmissionReminder(
						IPv4.fromIPv4Address(addressInt), bandwidth, timeout + 1000, data);
				String content = reminder.toJSONString();
				MarketManager.getInstance().getLowLevelController().pushMessageToHost("1.2.3.5", srcID, content);
				
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
