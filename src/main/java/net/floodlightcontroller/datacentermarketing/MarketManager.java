package net.floodlightcontroller.datacentermarketing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.internal.DeviceManagerImpl;

//logical layer for datacenter networking
public class MarketManager {
	
	//references to floodlight interfaces
	public MarketManager(){
		switches = new HashMap<Long, IOFSwitch>();
		devices = new HashMap<Long, IDevice>();
	}
	
	private DeviceManagerImpl deviceManager;
	private IFloodlightProviderService controller;
	
	public DeviceManagerImpl getDeviceManager() {
		return deviceManager;
	}

	public void setDeviceManager(DeviceManagerImpl deviceManager) {
		this.deviceManager = deviceManager;
	}
	
	
	public IFloodlightProviderService getController() {
		return controller;
	}

	public void setController(IFloodlightProviderService controller) {
		this.controller = controller;
	}
	
	private FlowUI flowUI;
	
	public FlowUI getFlowUI() {
		return flowUI;
	}

	public void setFlowUI(FlowUI flowUI) {
		this.flowUI = flowUI;
	}

	//internal hashmap for switches and devices
	private Map<Long, IOFSwitch> switches;
	private Map<Long, IDevice> devices;
	
	public Map<Long, IOFSwitch> getSwitches() {
		return switches;
	}

	public Map<Long, IDevice> getDevices() {
		return devices;
	}


	//basic functionality begins here..
	public void updateSwitches(){
		switches.clear();
		if(controller == null){
			System.out.println("Controller is null");
			return;
		}
		
		Map<Long, IOFSwitch> switchesMap = controller.getSwitches();
		if(switchesMap == null)
			return;
		
		Set<Entry<Long, IOFSwitch>> s = switchesMap.entrySet();
		Iterator<Entry<Long, IOFSwitch>> it = s.iterator();
		while(it.hasNext()){
			Entry<Long, IOFSwitch> entry = it.next();
			IOFSwitch ofSwitch = entry.getValue();
			Long longID = entry.getKey();
			switches.put(longID, ofSwitch);
		}
		
	}
	
	public void updateDevices(){
		devices.clear();
		if(deviceManager == null){
			System.out.println("DeviceManager is null");
			return;
		}
		Collection<? extends IDevice> devicesSet = deviceManager.getAllDevices();
		Iterator<? extends IDevice> iterator = devicesSet.iterator();
		while(iterator.hasNext()){
			IDevice device = iterator.next();
			devices.put(device.getDeviceKey(), device);
				
		}
		
	}
	
	
}
