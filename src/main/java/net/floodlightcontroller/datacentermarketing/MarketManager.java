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
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.devicemanager.internal.DeviceManagerImpl;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.topology.ITopologyService;


//logical layer for datacenter networking
public class MarketManager implements IFloodlightModule, IOFSwitchListener{
	
	protected IFloodlightProviderService controller;
	protected IDeviceService deviceManager;
	protected IStaticFlowEntryPusherService staticFlowEntryPusher;
	protected ITopologyService topologyManager;
	protected IRoutingService routingManager;
	
	
	//internal hashmap for switches and devices
	private Map<Long, IOFSwitch> switches;
	private Map<Long, IDevice> devices;
	
	private FlowUI flowUI;
	
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
		l.add(ITopologyService.class);
		l.add(IRoutingService.class);
		
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub
		
		//initialize the UI.
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
        topologyManager = context.getServiceImpl(ITopologyService.class);
        routingManager = context.getServiceImpl(IRoutingService.class);
        
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
		controller.addOFSwitchListener(this);
		
	}
	
	@Override
	public void addedSwitch(IOFSwitch sw) {
		// update the switches collection
		switches.put(sw.getId(), sw);
	}

	@Override
	public void removedSwitch(IOFSwitch sw) {
		// TODO Auto-generated method stub
		switches.remove(sw.getId());
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
			
			switches.put(ofSwitch.getId(), ofSwitch);
			
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
	
	
	//unknown implementation yet
	public long getSwitchPortBandwidth(Long switchKey, short portNum){
		IOFSwitch targetSwitch = switches.get(switchKey);
		if(targetSwitch == null) return -1;
		OFPhysicalPort port = targetSwitch.getPort(portNum);
		
		return 0;
	}

	
	
	//detect possible paths from two end hosts
	//using topology interface and routing service Floodlight provides	
	
	public ArrayList<Route> getNonLoopPaths(long srcID, long destID){
		
		updateDevices();
		updateSwitches();
		
		IDevice srcDev = devices.get(srcID);
		IDevice destDev = devices.get(destID);
		if(srcDev == null || destDev == null)
			return null;
		
		ArrayList<Route> routes = new ArrayList<Route>();
		
		/*
		 Get source and destination SwitchPort lists (aka attachment point of device)
		 so it is a multisource-multidestination graph searching problem
		 pick any pair of source and dest to find path set
		 then aggregate all possible together
		 */
		
		SwitchPort[] srcSwitchPorts = srcDev.getAttachmentPoints();
		SwitchPort[] destSwitchPorts = destDev.getAttachmentPoints();
		for(SwitchPort sourceSwitchPort : srcSwitchPorts){
			for(SwitchPort destSwitchPort : destSwitchPorts){
				ArrayList<Route> someRoutes = routingManager.getRoutes(sourceSwitchPort.getSwitchDPID(), destSwitchPort.getSwitchDPID(), true);
				routes.addAll(someRoutes);
				someRoutes = null;
			}
		}
		
		return routes;
	}
	
	
}
