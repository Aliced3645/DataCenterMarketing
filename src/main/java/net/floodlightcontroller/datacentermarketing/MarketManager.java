package net.floodlightcontroller.datacentermarketing;


import java.util.Map;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.datacentermarketing.controller.LowLevelController;
import net.floodlightcontroller.devicemanager.IDevice;

//logical layer for datacenter networking
//It implements data center marketing via the aid of low level controller and scheduler
//On the top of it runs the UI class

//implemented as a singleton
public class MarketManager {
	
	private static MarketManager _instance = null;
	
	public static MarketManager getInstance(){
		if(_instance == null){
			_instance = new MarketManager();
		}
		return _instance;
	}
	
	// internal hashmap for switches and devices
	private Map<Long, IOFSwitch> switches;
	private Map<Long, IDevice> devices;
	private LowLevelController lowLevelController;

	private FlowUI flowUI;

	private MarketManager() {
		// initialize the UI.
		new UIThread();
		while (UIThread.getFlowUI() == null) {
			System.out.println("Waiting....");
		}
		flowUI = UIThread.getFlowUI();
		setFlowUI(flowUI);
		flowUI.setMarketManager(this);
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
	
	public void setSwitches(Map<Long, IOFSwitch> switches){
		this.switches = switches;
	}
	
	public void setDevices(Map<Long, IDevice> devices){
		this.devices = devices;
	}
	
	public void updateDevices(){
		lowLevelController.updateDevices();
	}
	
	public void updateSwitches(){
		lowLevelController.updateSwitches();
	}

	public LowLevelController getLowLevelController() {
		return lowLevelController;
	}

	public void setLowLevelController(LowLevelController lowLevelController) {
		this.lowLevelController = lowLevelController;
	}
	
}
