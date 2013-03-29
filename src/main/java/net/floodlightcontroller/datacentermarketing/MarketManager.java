package net.floodlightcontroller.datacentermarketing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.openflow.protocol.OFPhysicalPort;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.benchmarkcontroller.FlowPusherController;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.datacentermarketing.controller.LowLevelController;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.devicemanager.internal.DeviceManagerImpl;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.topology.ITopologyService;

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
