package net.floodlightcontroller.datacentermarketing.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFType;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.counter.ICounterStoreService;
import net.floodlightcontroller.datacentermarketing.MarketManager;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.topology.ITopologyService;

// Low level controller interacts with Floodlight, all above modules, access SDN only by this class.
// The controller provides all interfaces with for Datacenter marketing over SDN.
//the low level controller does the following stuff:
/*
 * 1. Assign a switch, and flow properties, to reserve bandwidth
 * 
 */
public class LowLevelController implements IFloodlightModule, IOFSwitchListener, IOFMessageListener {

	protected IFloodlightProviderService controller;
	protected IDeviceService deviceManager;
	protected IStaticFlowEntryPusherService staticFlowEntryPusher;
	protected ITopologyService topologyManager;
	protected IRoutingService routingManager;
	protected ICounterStoreService counterStore;
	
	// internal hashmap for switches and devices
	private Map<Long, IOFSwitch> switches;
	private Map<Long, IDevice> devices;

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
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IDeviceService.class);
		l.add(IStaticFlowEntryPusherService.class);
		l.add(ITopologyService.class);
		l.add(IRoutingService.class);
		l.add(ICounterStoreService.class);
		return l;
	}

	
	private void buildDatacenterMarketing(){
		/* attach this module to MarketManager class */
		MarketManager marketManager = MarketManager.getInstance();
		marketManager.setLowLevelController(this);
		marketManager.setDevices(this.devices);
		marketManager.setSwitches(this.switches);
	}
	
	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub

		controller = context.getServiceImpl(IFloodlightProviderService.class);
		deviceManager = context.getServiceImpl(IDeviceService.class);
		staticFlowEntryPusher = context
				.getServiceImpl(IStaticFlowEntryPusherService.class);
		topologyManager = context.getServiceImpl(ITopologyService.class);
		routingManager = context.getServiceImpl(IRoutingService.class);
		counterStore = context.getServiceImpl(ICounterStoreService.class);
		
		try {
			switches = new HashMap<Long, IOFSwitch>();
			devices = new HashMap<Long, IDevice>();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		buildDatacenterMarketing();
	}

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

	public Map<Long, IOFSwitch> getSwitches() {
		return switches;
	}

	public Map<Long, IDevice> getDevices() {
		return devices;
	}

	// basic functionality begins here..
	public void updateSwitches() {
		switches.clear();
		if (controller == null) {
			System.out.println("Controller is null");
			return;
		}

		Map<Long, IOFSwitch> switchesMap = controller.getSwitches();
		if (switchesMap == null)
			return;

		Set<Entry<Long, IOFSwitch>> s = switchesMap.entrySet();
		Iterator<Entry<Long, IOFSwitch>> it = s.iterator();
		while (it.hasNext()) {
			Entry<Long, IOFSwitch> entry = it.next();
			IOFSwitch ofSwitch = entry.getValue();

			switches.put(ofSwitch.getId(), ofSwitch);

		}

	}

	public void updateDevices() {
		devices.clear();
		if (deviceManager == null) {
			System.out.println("DeviceManager is null");
			return;
		}
		Collection<? extends IDevice> devicesSet = deviceManager
				.getAllDevices();
		Iterator<? extends IDevice> iterator = devicesSet.iterator();
		while (iterator.hasNext()) {
			IDevice device = iterator.next();
			devices.put(device.getDeviceKey(), device);

		}

	}

	// detect possible paths from two end hosts
	// using topology interface and routing service Floodlight provides

	public ArrayList<Route> getNonLoopPaths(long srcID, long destID) {

		updateDevices();
		updateSwitches();

		IDevice srcDev = devices.get(srcID);
		IDevice destDev = devices.get(destID);
		if (srcDev == null || destDev == null)
			return null;

		ArrayList<Route> routes = new ArrayList<Route>();

		/*
		 * Get source and destination SwitchPort lists (aka attachment point of
		 * device) so it is a multisource-multidestination graph searching
		 * problem pick any pair of source and dest to find path set then
		 * aggregate all possible together
		 */
		SwitchPort[] srcSwitchPorts = srcDev.getAttachmentPoints();
		SwitchPort[] destSwitchPorts = destDev.getAttachmentPoints();
		for (SwitchPort sourceSwitchPort : srcSwitchPorts) {
			for (SwitchPort destSwitchPort : destSwitchPorts) {
				ArrayList<Route> someRoutes = routingManager.getRoutes(
						sourceSwitchPort.getSwitchDPID(),
						destSwitchPort.getSwitchDPID(), true);
				routes.addAll(someRoutes);
				someRoutes = null;
			}
		}

		return routes;
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * In order to set the bandwidth of a particular flow in a particular switch:
	 * 1. Get the switch object / ID
	 * 2. Be able to push an entry 
	 * 3. Set the maximum counter for bytes ( so as to control the speed , using the counter of queue!)
	 * 4. For every second, refresh the counter to ensure the bandwidth
	 */
	
	/*  The mapping from flow to queues takes place within the OpenFlow protocol.
	 *	Assuming that a queue is already configured, the user can associate a flow with an OFPAT_ENQUEUE 
	 *	action which forwards the packet through the specifc queue in that port. 
	 *	Note that an enqueue action should override any TOS/VLAN_PCP related behavior 
	 *	that is potentially defined in the switch. 
	 *	In all cases, the packet should not change due to an enqueue action. 
	 *  If the switch needs to set TOS/PCP bits for internal handling, the original values should 
	 *  be restored before sending the packet out.
	 */
	
	//Code borrowed from QueueCreator.
	
	
	//add an entry in the switch's flow table
	//public boolean addFlowEntry(){
		
	//}
	
	

}
