package net.floodlightcontroller.datacentermarketing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.datacentermarketing.Scheduling.Scheduler;
import net.floodlightcontroller.datacentermarketing.controller.LowLevelController;
import net.floodlightcontroller.datacentermarketing.logic.BiddingClock;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.routing.Route;

//logical layer for datacenter networking
//It implements data center marketing via the aid of low level controller and scheduler
//On the top of it runs the UI class

//implemented as a singleton
public class MarketManager {

	private static MarketManager _instance = null;

	public static MarketManager getInstance() throws IOException,
			InterruptedException, ExecutionException {
		if (_instance == null) {
			_instance = new MarketManager();
		}
		return _instance;
	}

	// internal hashmap for switches and devices
	private Map<Long, IOFSwitch> switches;
	private Map<Long, IDevice> devices;
	private LowLevelController lowLevelController;
	private BiddingClock clock;
	private FlowUI flowUI;

	public long getCurrentTime() {
		return clock.getCurrentTime();
	}

	private MarketManager() throws IOException, InterruptedException,
			ExecutionException {
		// initialize the UI.
		new UIThread();
		while (UIThread.getFlowUI() == null) {
			try {

				// do what you want to do before sleeping
				Thread.sleep(1000);// sleep for 1000 ms
				// do what you want to do after sleeptig
			} catch (Exception ie) {
				// If this thread was intrrupted by nother thread
			}
		}
		flowUI = UIThread.getFlowUI();
		setFlowUI(flowUI);
		flowUI.setMarketManager(this);

		// initialize the timer
		clock = BiddingClock.getInstance();

	}

	public FlowUI getFlowUI() {
		return flowUI;
	}

	public void setFlowUI(FlowUI flowUI) {
		this.flowUI = flowUI;
	}

	public Map<Long, IOFSwitch> getSwitches() {
		return switches;
	}

	public Map<Long, IDevice> getDevices() {
		return devices;
	}

	public void setSwitches(Map<Long, IOFSwitch> switches) {
		this.switches = switches;
	}

	public void setDevices(Map<Long, IDevice> devices) {
		this.devices = devices;
	}

	public void updateDevices() {
		lowLevelController.updateDevices();
	}

	public void updateSwitches() throws IOException, InterruptedException,
			ExecutionException {
		lowLevelController.updateSwitches();
	}

	public LowLevelController getLowLevelController() {
		return lowLevelController;
	}

	public void setLowLevelController(LowLevelController lowLevelController) {
		this.lowLevelController = lowLevelController;
	}

	public ArrayList<Route> getNonLoopPaths(long srcID, long destID)
			throws IOException, InterruptedException, ExecutionException {
		return lowLevelController.getNonLoopPaths(srcID, destID);
	}

}
