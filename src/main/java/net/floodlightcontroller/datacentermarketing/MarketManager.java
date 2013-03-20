package net.floodlightcontroller.datacentermarketing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.internal.DeviceManagerImpl;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;

//logical layer for datacenter networking
public class MarketManager implements IFloodlightModule, IOFSwitchListener{
	
	protected IFloodlightProviderService controller;
	protected IDeviceService deviceManager;
	protected IStaticFlowEntryPusherService staticFlowEntryPusher;
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// TODO Auto-generated method stub
		Collection<Class<? extends IFloodlightService>> l =
				new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IDeviceService.class);
		l.add(IStaticFlowEntryPusherService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub
		new UIThread();
        while(UIThread.getFlowUI() == null){
        	System.out.println("Waiting....");
        }
        flowUI = UIThread.getFlowUI();
        setFlowUI(flowUI);
        flowUI.setMarketManager(this);
        
		
		controller = context.getServiceImpl(IFloodlightProviderService.class);
		deviceManager = context.getServiceImpl(IDeviceService.class);
		staticFlowEntryPusher = context.getServiceImpl(IStaticFlowEntryPusherService.class);
        
		try {
        	switches = new HashMap<Long, IOFSwitch>();
    		devices = new HashMap<Long, IDevice>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//switch listener interfaces
	@Override
	public void startUp(FloodlightModuleContext context) {
		// TODO Auto-generated method stub
		//controller.addOFSwitchListener(this);
		
	}
	
	@Override
	public void addedSwitch(IOFSwitch sw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removedSwitch(IOFSwitch sw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void switchPortChanged(Long switchId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public IDeviceService getDeviceManager() {
		return deviceManager;
	}

	public void setDeviceManager(IDeviceService deviceManager) {
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
	
	public long getSwitchPortBandwidth(Long switchKey, short portNum){
		IOFSwitch targetSwitch = switches.get(switchKey);
		if(targetSwitch == null) return -1;
		OFPhysicalPort port = targetSwitch.getPort(portNum);
		
		
		return 0;
	}




	
	//detect possible paths from two end hosts
	//using topology interface Floodlight provides
	
	
}
