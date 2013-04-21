package net.floodlightcontroller.datacentermarketing.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.openflow.protocol.OFBarrierRequest;
import org.openflow.protocol.OFEchoRequest;
import org.openflow.protocol.OFError;
import org.openflow.protocol.OFFeaturesReply;
import org.openflow.protocol.OFFeaturesRequest;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPacketQueue;
import org.openflow.protocol.OFPhysicalPort;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFQueueGetConfigReply;
import org.openflow.protocol.OFQueueGetConfigRequest;
import org.openflow.protocol.OFQueueProp;
import org.openflow.protocol.OFType;
import org.openflow.protocol.OFVendor;
import org.openflow.protocol.OFQueueProp.OFQueuePropType;
import org.openflow.protocol.Wildcards;
import org.openflow.protocol.Wildcards.Flag;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.util.HexString;
import org.openflow.util.U16;
import org.openflow.vendor.openflow.OFOpenFlowVendorData;
import org.openflow.vendor.openflow.OFQueueDeleteVendorData;
import org.openflow.vendor.openflow.OFQueueModifyVendorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Wildcard;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.annotations.LogMessageDoc;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.util.AppCookie;
import net.floodlightcontroller.counter.ICounterStoreService;
import net.floodlightcontroller.datacentermarketing.MarketManager;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packetstreamer.thrift.SwitchPortTuple;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.staticflowentry.StaticFlowEntryPusher;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.topology.NodePortTuple;

// Low level controller interacts with Floodlight, all above modules, access SDN only by this class.
// The controller provides all interfaces with for Datacenter marketing over SDN.
//the low level controller does the following stuff:

//The class is used to map the flow entry to a ( queue - port - switch ) tuple
//In this project, there must be one to one relation

public class LowLevelController implements IOFSwitchListener,
	IOFMessageListener, IFloodlightModule
{

    protected IFloodlightProviderService controller;
    protected IDeviceService deviceManager;
    protected IStaticFlowEntryPusherService staticFlowEntryPusher;
    protected ITopologyService topologyManager;
    protected IRoutingService routingManager;
    protected ICounterStoreService counterStore;

    // internal hashmap for switches and devices
    private Map<Long, IOFSwitch> switches;
    private Map<Long, IDevice> devices;
    protected IFloodlightProviderService floodlightProvider;

    private Future<OFFeaturesReply> future;
    protected static Logger log = LoggerFactory
	    .getLogger(LowLevelController.class);

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices()
    {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls()
    {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies()
    {
	// TODO Auto-generated method stub
	Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
	l.add(IFloodlightProviderService.class);
	l.add(IDeviceService.class);
	l.add(IStaticFlowEntryPusherService.class);
	l.add(ITopologyService.class);
	l.add(IRoutingService.class);
	l.add(ICounterStoreService.class);
	l.add(IFloodlightProviderService.class);
	return l;
    }

    private void buildDatacenterMarketing()
    {
	/* attach this module to MarketManager class */
	MarketManager marketManager = MarketManager.getInstance();
	marketManager.setLowLevelController(this);
	marketManager.setDevices(this.devices);
	marketManager.setSwitches(this.switches);
    }

    @Override
    public void init(FloodlightModuleContext context)
	    throws FloodlightModuleException
    {
	// TODO Auto-generated method stub
	for (int i = 0; i < 100; i++)
	    System.out.println("INITING!!!!!!!!!!!");
	floodlightProvider = context
		.getServiceImpl(IFloodlightProviderService.class);
	controller = context.getServiceImpl(IFloodlightProviderService.class);
	deviceManager = context.getServiceImpl(IDeviceService.class);
	staticFlowEntryPusher = context
		.getServiceImpl(IStaticFlowEntryPusherService.class);
	topologyManager = context.getServiceImpl(ITopologyService.class);
	routingManager = context.getServiceImpl(IRoutingService.class);
	counterStore = context.getServiceImpl(ICounterStoreService.class);

	try
	{
	    switches = new HashMap<Long, IOFSwitch>();
	    devices = new HashMap<Long, IDevice>();

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

	buildDatacenterMarketing();
    }

    @Override
    public void startUp(FloodlightModuleContext context)
    {
	// TODO Auto-generated method stub
	controller.addOFSwitchListener(this);
	controller.addOFMessageListener(OFType.QUEUE_GET_CONFIG_REPLY, this);
	controller.addOFMessageListener(OFType.ERROR, this);
	controller.addOFMessageListener(OFType.FEATURES_REPLY, this);
	controller.addOFMessageListener(OFType.ECHO_REPLY, this);
	controller.addOFMessageListener(OFType.PACKET_IN, this);
    }

    @Override
    public void addedSwitch(IOFSwitch sw)
    {
	// update the switches collection
	switches.put(sw.getId(), sw);
    }

    @Override
    public void removedSwitch(IOFSwitch sw)
    {
	// TODO Auto-generated method stub
	switches.remove(sw.getId());
    }

    @Override
    public void switchPortChanged(Long switchId)
    {
	// TODO Auto-generated method stub

    }

    @Override
    public String getName()
    {
	// TODO Auto-generated method stub
	return "LowLevelController";
    }

    public Map<Long, IOFSwitch> getSwitches()
    {
	return switches;
    }

    public Map<Long, IDevice> getDevices()
    {
	return devices;
    }

    // send queue queries to port
    private void sendQueueQuery(IOFSwitch ofSwitch, short portNumber)
	    throws IOException
    {
	OFQueueGetConfigRequest queueQueryMessage = new OFQueueGetConfigRequest();
	queueQueryMessage.setPortNumber(portNumber);
	ofSwitch.write(queueQueryMessage, null);
	return;
    }

    // basic functionality begins here..
    public void updateSwitches() throws IOException, InterruptedException,
	    ExecutionException
    {

	switches.clear();

	if (controller == null)
	{
	    System.out.println("Controller is null");
	    return;
	}

	Map<Long, IOFSwitch> switchesMap = controller.getSwitches();
	if (switchesMap == null)
	    return;

	Set<Entry<Long, IOFSwitch>> s = switchesMap.entrySet();
	Iterator<Entry<Long, IOFSwitch>> it = s.iterator();

	while (it.hasNext())
	{

	    Entry<Long, IOFSwitch> entry = it.next();
	    IOFSwitch ofSwitch = entry.getValue();

	    switches.put(ofSwitch.getId(), ofSwitch);

	    // query the switch to get the update..
	    future = ofSwitch.querySwitchFeaturesReply();
	    ofSwitch.flush();
	}

    }

    public void updateDevices()
    {
	devices.clear();
	if (deviceManager == null)
	{
	    System.out.println("DeviceManager is null");
	    return;
	}
	Collection<? extends IDevice> devicesSet = deviceManager
		.getAllDevices();
	Iterator<? extends IDevice> iterator = devicesSet.iterator();
	while (iterator.hasNext())
	{
	    IDevice device = iterator.next();
	    devices.put(device.getDeviceKey(), device);

	}

    }

    // detect possible paths from two end hosts
    // using topology interface and routing service Floodlight provides

    public ArrayList<Route> getNonLoopPaths(long srcID, long destID)
	    throws IOException, InterruptedException, ExecutionException
    {

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

	for (SwitchPort sourceSwitchPort : srcSwitchPorts)
	{
	    for (SwitchPort destSwitchPort : destSwitchPorts)
	    {
		ArrayList<Route> someRoutes = routingManager.getRoutes(
			sourceSwitchPort.getSwitchDPID(),
			destSwitchPort.getSwitchDPID(), true);
		routes.addAll(someRoutes);
		someRoutes = null;
	    }
	}
	// routes = routingManager.getRoutes(srcID, destID, true);
	return routes;
    }

    // /**
    // * flow pusher to reserve a route
    // *
    // * @param {Route} route the route to reserve
    // * @param {long} bandwidth the bandwidth to reserve
    // */
    // private void pushFlow(Route route, long bandwidth) {
    // //generate a random flow
    // String srcIp = IPv4.fromIPv4Address(flowCount);
    // String dstIp = IPv4.fromIPv4Address(flowCount * 2);
    // ArrayList<OFAction> actionsTo = new ArrayList<OFAction>();
    // OFFlowMod flowMod = new OFFlowMod();
    // flowMod.setType(OFType.FLOW_MOD);
    // OFAction outputTo = new OFActionOutput((short) ran.nextInt(30));
    // actionsTo.add(outputTo);
    // OFMatch match = new OFMatch();
    // match.setNetworkDestination(IPv4.toIPv4Address(dstIp));
    // match.setNetworkSource(IPv4.toIPv4Address(srcIp));
    // match.setDataLayerType(Ethernet.TYPE_IPv4);
    // flowMod.setActions(actionsTo);
    // flowMod.setMatch(match);
    // //flowMod.setHardTimeout((short)20);
    // logger.info("pushing the flow:" + ("Flow#" + flowCount
    // + " srcIp:" + match.getNetworkSource()
    // + " srcPort:" + match.getTransportSource()
    // + " dstIp:" + match.getNetworkDestination()
    // + " dstPort:" + match.getTransportDestination()));
    // staticFlowEntryPusher.addFlow(("Flow#" + flowCount), flowMod, dp);
    // flowCount ++;
    // if(flowCount == TOTAL_FLOWS) {
    // stop = true;
    // try {
    // createRecorder.close();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    // }
    //
    /**
     * debug information
     */
    private void debug(String str)
    {
	System.out.println("LowLevelController: " + str);
    }

    /**
     * Writes an OFFlowMod to a switch
     * 
     * @param sw
     *            The IOFSwitch to write to
     * @param flowMod
     *            The OFFlowMod to write
     */
    @LogMessageDoc(level = "ERROR", message = "Tried to write OFFlowMod to {switch} but got {error}", explanation = "An I/O error occured while trying to write a "
	    + "static flow to a switch", recommendation = LogMessageDoc.CHECK_SWITCH)
    private void writeFlowModToSwitch(IOFSwitch sw, OFFlowMod flowMod)
    {
	try
	{
	    log.debug("\nTrying to add the following flowmod: \n"
		    + flowMod.toString() + "\nto Switch\n" + sw.toString()
		    + "\n");
	    sw.write(flowMod, null);
	    sw.flush();
	}
	catch (Exception e)
	{
	    log.error("Tried to write OFFlowMod to {} but failed: {}",
		    HexString.toHexString(sw.getId()), e.getMessage());
	}
    }

    /**
     * the following code probe through a route to obtain latency
     * 
     * 
     * 
     */

    private class TimePair
    {
	public long start;
	private long end;
	private boolean isDone;

	/*
	 * public synchronized boolean isDone() { return isDone; }
	 * 
	 * public synchronized void setDone(boolean isDone) { this.isDone =
	 * isDone; }
	 */

	@SuppressWarnings("unused")
	public synchronized long getEnd()
	{
	    return end;
	}

	public synchronized void setEnd(long end)
	{
	    this.end = end;
	    isDone = true;
	}

	public TimePair(long s, long e)
	{
	    start = s;
	    end = s;
	}

    }

    public boolean installRoute(long srcID, long destID, Route rt,
	    long bandwidth) throws Exception
    {
	List<NodePortTuple> switchesPorts = rt.getPath();
	if (switchesPorts.size() < 2)
	{
	    debug("Route length is not right.");
	}
	if (switchesPorts.size() % 2 == 1)
	{
	    debug("mismatched switch in-out port number!");
	}
	int index = 0;
	IOFSwitch startSW = null;
	// get devices
	IDevice srcDev = devices.get(srcID);
	IDevice destDev = devices.get(destID);
	// set up match rule
	OFMatch match = new OFMatch();
	match.setDataLayerDestination(destDev.getMACAddressString());
	match.setDataLayerSource(srcDev.getMACAddressString());

	while (index < switchesPorts.size())
	{

	}

	return false;
    }

    // bench marks cache
    private HashMap<String, TimePair> routesBenchMarks = new HashMap();

    public void ping(long start, long end) throws Exception
    {
	routesBenchMarks.clear();
	// get the routes
	ArrayList<Route> routes = getNonLoopPaths(start, end);
	for (Route rt : routes)
	{
	    probeLatency(rt);
	}

    }

    private void probeLatency(Route rt) throws Exception
    {

	// TODO check cache

	// for the first one, we make a packet and send it to the first switch
	// the last switch send the packet back, as required by an action

	// checks for validation
	List<NodePortTuple> switchesPorts = rt.getPath();
	if (switchesPorts.size() < 2)
	{
	    debug("Route length is not right.");
	}
	if (switchesPorts.size() % 2 == 1)
	{
	    debug("mismatched switch in-out port number!");
	}

	// get the first switch
	int index = 0;

	NodePortTuple first = switchesPorts.get(index);
	long nodePid = first.getNodeId();
	IOFSwitch startSw = switches.get(nodePid);

	// action
	OFAction outputTo = new OFActionOutput().setPort(first.getPortId());
	outputTo.setLength(Short.MAX_VALUE);// we want whole packet back to
					    // controller
	// action list
	ArrayList<OFAction> actionsTo = new ArrayList<OFAction>();
	actionsTo.add(outputTo);
	// match
	OFMatch match = new OFMatch().setWildcards(OFMatch.OFPFW_ALL
		& (~OFMatch.OFPFW_NW_SRC_MASK) & (~OFMatch.OFPFW_NW_DST_MASK));
	match.setNetworkSource(IPv4.toIPv4Address("1.2.3.4"));
	match.setNetworkDestination(IPv4.toIPv4Address("1.2.3.4"));

	// match.setDataLayerType(Ethernet.TYPE_IPv4);
	log.debug("set match " + match.toString());

	// flow mod
	OFFlowMod flowMod = (OFFlowMod) floodlightProvider
		.getOFMessageFactory().getMessage(OFType.FLOW_MOD);

	flowMod.setIdleTimeout(Short.MAX_VALUE).setHardTimeout(Short.MAX_VALUE)
		.setBufferId(OFPacketOut.BUFFER_ID_NONE)
		.setCookie(AppCookie.makeCookie(0, 0))
		.setCommand(OFFlowMod.OFPFC_ADD).setMatch(match)
		.setActions(actionsTo).setLengthU(OFFlowMod.MINIMUM_LENGTH);
	flowMod.setFlags(OFFlowMod.OFPFF_SEND_FLOW_REM);

	log.debug("match in flowmod is now : " + flowMod.getMatch().toString());

	writeFlowModToSwitch(startSw, flowMod);
	sendBarrier(startSw);

	// set the middle ones that are on same switch
	index++;

	while (index < switchesPorts.size() - 1)
	{
	    // get the switch
	    NodePortTuple firstInPair = switchesPorts.get(index);
	    nodePid = firstInPair.getNodeId();
	    IOFSwitch sw = switches.get(nodePid);
	    short ingressPortPid = firstInPair.getPortId();
	    index++;
	    NodePortTuple secondInPair = switchesPorts.get(index);
	    short egressPortPid = secondInPair.getPortId();
	    index++;

	    if (firstInPair.getNodeId() != secondInPair.getNodeId())
	    {
		debug("mismatched switch id!");
	    }

	    actionsTo.clear();
	    flowMod = (OFFlowMod) floodlightProvider.getOFMessageFactory()
		    .getMessage(OFType.FLOW_MOD);
	    flowMod.setType(OFType.FLOW_MOD);
	    outputTo = new OFActionOutput().setPort(egressPortPid);
	    outputTo.setLength(Short.MAX_VALUE);// we want whole packet back to
	    // controller

	    actionsTo.add(outputTo);
	    match = new OFMatch();

	    try
	    {
		match.setInputPort(ingressPortPid);
		match.setNetworkDestination(IPv4.toIPv4Address(InetAddress
			.getLocalHost().getHostAddress()));
		match.setNetworkSource(IPv4.toIPv4Address(InetAddress
			.getLocalHost().getHostAddress()));
	    }
	    catch (Exception e)
	    {
		debug("error geting local controler iP!");
		throw e;
	    }
	    match.setDataLayerType(Ethernet.TYPE_IPv4);
	    flowMod.setActions(actionsTo);
	    flowMod.setMatch(match);

	    writeFlowModToSwitch(sw, flowMod);
	    sendBarrier(sw);
	}
	// last port
	NodePortTuple last = switchesPorts.get(index);
	nodePid = last.getNodeId();
	IOFSwitch endSw = switches.get(nodePid);

	outputTo = new OFActionOutput().setPort(OFPort.OFPP_CONTROLLER
		.getValue());
	outputTo.setLength(Short.MAX_VALUE);// we want whole packet back to
					    // controller

	actionsTo.clear();
	actionsTo.add(outputTo);

	match = new OFMatch();
	try
	{
	    match.setInputPort(last.getPortId());
	    match.setNetworkDestination(IPv4.toIPv4Address(InetAddress
		    .getLocalHost().getHostAddress()));
	    match.setNetworkSource(IPv4.toIPv4Address(InetAddress
		    .getLocalHost().getHostAddress()));
	    match.setDataLayerType(Ethernet.TYPE_IPv4);
	}
	catch (Exception e)
	{
	    debug("error geting local controler iP!");
	    throw e;
	}
	flowMod = (OFFlowMod) floodlightProvider.getOFMessageFactory()
		.getMessage(OFType.FLOW_MOD);
	flowMod.setType(OFType.FLOW_MOD);
	flowMod.setActions(actionsTo);
	flowMod.setMatch(match);

	writeFlowModToSwitch(endSw, flowMod);
	sendBarrier(endSw);

	// send the probe packet which is an ip v4 packet we made up
	IPv4 probe = new IPv4();
	probe.setSourceAddress(InetAddress.getLocalHost().getHostAddress());
	probe.setDestinationAddress(InetAddress.getLocalHost().getHostAddress());
	// put the identifier string in the body
	probe.setPayload(new Data(rt.toString().getBytes()));

	// packet out ofoutmessage
	OFPacketOut probeMsg = new OFPacketOut();
	probeMsg.setBufferId(OFPacketOut.BUFFER_ID_NONE);// our data is in the
	probeMsg.setPacketData(probe.serialize());

	// record the time
	routesBenchMarks.put(rt.toString(), new TimePair(System.nanoTime(), 0));

	// send it out

	// last step set length
	probeMsg.setLength(U16.t(OFPacketOut.MINIMUM_LENGTH
		+ probeMsg.getActionsLength() + probeMsg.getPacketData().length));

	startSw.write(probeMsg, null);// TODO context?

	debug("\n\n\n\n\n\npacket out, now wait for inmessage");

	return;
    }

    private void finishRouteBenchMark(OFPacketIn msg)
    {

	// get the data out of the msg
	try
	{
	    IPv4 probe = new IPv4();
	    probe.deserialize(msg.getPacketData(), 0,
		    msg.getPacketData().length);
	    String id = ((Data) probe.getPayload()).toString();

	    if (routesBenchMarks.containsKey(id))
	    {
		// update the back time
		routesBenchMarks.get(id).setEnd(System.nanoTime());
		debug("\n\n\n\n\n\n\n\n\nActually worked! : " + id);
		// TODO tear down the route

	    }
	    else
	    {
		debug("None idetified id: " + id);

	    }
	}
	catch (Exception e)
	{
	    /* debug("Exception : " + e.toString()); */
	    return;
	}

    }

    private void sendBarrier(IOFSwitch sw)
    {
	OFBarrierRequest m = new OFBarrierRequest();
	try
	{
	    sw.write(m, null);
	}
	catch (IOException e)
	{
	    log.error("Tried to write to switch {} but got {}", sw.getId(),
		    e.getMessage());
	}

	sw.flush();
    }

    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name)
    {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name)
    {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public net.floodlightcontroller.core.IListener.Command receive(
	    IOFSwitch sw, OFMessage msg, FloodlightContext cntx)
    {

	switch (msg.getType())
	{
	// get the reply, update related hash tables for queue/port/switch
	// management

	case QUEUE_GET_CONFIG_REPLY:

	    System.out.println("Got a Queue Config Reply!!");
	    OFQueueGetConfigReply reply = (OFQueueGetConfigReply) msg;
	    List<OFPacketQueue> queues = reply.getQueues();

	    System.out.println("This is the port: " + reply.getPortNumber());

	    System.out.println("Number of queues: " + queues.size());

	    for (OFPacketQueue queue : queues)
	    {
		long qid = queue.getQueueId();
		List<OFQueueProp> props = queue.getProperties();
		System.out.println(" qid = " + qid);
		System.out.println(" num props = " + props.size());
		for (OFQueueProp prop : props)
		{
		    System.out.println(" type = " + prop.getType());
		    System.out.println(" rate = " + prop.getRate());
		}
	    }
	    break;

	case FEATURES_REPLY:
	    System.out.println("\n\n feature reply!! \n\n");
	    break;

	case ECHO_REPLY:
	    System.out.println("\n\n echo reply!! \n\n");
	    break;

	case ERROR:
	    // OFRBRC_BAD_TYPE
	    OFError error = (OFError) msg;
	    // System.out.println("wowow");
	    break;

	case PACKET_IN:
	    finishRouteBenchMark((OFPacketIn) msg);
	    break;
	default:
	    System.out.println("unexpected message type: " + msg.getType());
	}
	;

	return Command.CONTINUE;
    }

    private void getAllQueueConfigs(IOFSwitch sw)
    {
	OFQueueGetConfigRequest m = new OFQueueGetConfigRequest();

	Collection<OFPhysicalPort> ports = sw.getPorts();

	for (OFPhysicalPort port : ports)
	{
	    if (U16.f(port.getPortNumber()) >= U16
		    .f(OFPort.OFPP_MAX.getValue()))
	    {
		continue;
	    }

	    System.out.println("Sending a queue get config to: "
		    + port.getPortNumber());

	    m.setPortNumber(port.getPortNumber());

	    try
	    {
		sw.write(m, null);
	    }
	    catch (IOException e)
	    {
		System.out.println("Tried to write to switch "
			+ sw.getStringId() + "but got " + e.getMessage());
	    }
	}

	sw.flush();
    }

    private void sendOFVendorData(IOFSwitch sw, OFOpenFlowVendorData data)
    {
	OFVendor msg = (OFVendor) this.controller.getOFMessageFactory()
		.getMessage(OFType.VENDOR);
	msg.setVendor(OFOpenFlowVendorData.OF_VENDOR_ID);

	msg.setVendorData(data);
	msg.setLengthU(OFVendor.MINIMUM_LENGTH + data.getLength());

	try
	{
	    sw.write(msg, null);
	}
	catch (IOException e)
	{
	    System.out.println("Tried to write to switch " + sw.getStringId()
		    + "but got " + e.getMessage());
	}

	sw.flush();
    }

    private void createQueue(IOFSwitch sw, short portNumber, int queueId,
	    short rate)
    {
	OFQueueProp prop = new OFQueueProp();
	prop.setType(OFQueuePropType.OFPQT_MIN_RATE);
	prop.setRate(rate);

	OFPacketQueue queue = new OFPacketQueue(queueId);
	queue.setProperties(new ArrayList<OFQueueProp>(Arrays.asList(prop)));

	OFQueueModifyVendorData queueModifyData = new OFQueueModifyVendorData();
	queueModifyData.setPortNumber(portNumber);
	queueModifyData.setQueues(new ArrayList<OFPacketQueue>(Arrays
		.asList(queue)));

	sendOFVendorData(sw, queueModifyData);
    }

    private void deleteQueue(IOFSwitch sw, short portNumber, int queueId)
    {
	OFPacketQueue queue = new OFPacketQueue(queueId);

	OFQueueDeleteVendorData queueDeleteData = new OFQueueDeleteVendorData();
	queueDeleteData.setPortNumber(portNumber);
	queueDeleteData.setQueues(new ArrayList<OFPacketQueue>(Arrays
		.asList(queue)));

	sendOFVendorData(sw, queueDeleteData);
    }

    /*
     * In order to set the bandwidth of a particular flow in a particular
     * switch: 1. Get the switch object / ID 2. Be able to push an entry 3. Set
     * the maximum counter for bytes ( so as to control the speed , using the
     * counter of queue!) 4. For every second, refresh the counter to ensure the
     * bandwidth
     */

    /*
     * The mapping from flow to queues takes place within the OpenFlow protocol.
     * Assuming that a queue is already configured, the user can associate a
     * flow with an OFPAT_ENQUEUE action which forwards the packet through the
     * specifc queue in that port. Note that an enqueue action should override
     * any TOS/VLAN_PCP related behavior that is potentially defined in the
     * switch. In all cases, the packet should not change due to an enqueue
     * action. If the switch needs to set TOS/PCP bits for internal handling,
     * the original values should be restored before sending the packet out.
     */

    // Code borrowed from QueueCreator.

    // add an entry in the switch's flow table
    public boolean addFlowEntry()
    {

	return false;
    }

    // really assign routes for a flow (with queue creation)

    public boolean designateActualRoutes()
    {

	return false;
    }

    // Provided for the scheduler: get the maximum bandwidth of a port of
    // specified switch

    public Collection<MaxBandwidth> getPortMaxBandwidthForSwitch(Long switchID,
	    short portNumber)
    {
	IOFSwitch ofSwitch = switches.get(switchID);
	OFPhysicalPort port = ofSwitch.getPort(portNumber);
	int currentFeatures = port.getCurrentFeatures();
	LinkedList<MaxBandwidth> maxBandwidths = new LinkedList<MaxBandwidth>();
	// test first 10 bits
	int i = 0;
	while (i <= 9)
	{
	    MaxBandwidth mb = null;
	    int mask = 1 << i;
	    int result = currentFeatures & mask;
	    if (result != 0)
	    {
		switch (i)
		{
		case 0:
		    mb = new MaxBandwidth(10, BandwidthUnit.Megabyte, false);
		    break;
		case 1:
		    mb = new MaxBandwidth(10, BandwidthUnit.Megabyte, true);
		    break;
		case 2:
		    mb = new MaxBandwidth(100, BandwidthUnit.Megabyte, false);
		    break;
		case 3:
		    mb = new MaxBandwidth(100, BandwidthUnit.Megabyte, true);
		    break;
		case 4:
		    mb = new MaxBandwidth(1, BandwidthUnit.Gigabyte, false);
		    break;
		case 5:
		    mb = new MaxBandwidth(1, BandwidthUnit.Gigabyte, true);
		    break;
		case 6:
		    mb = new MaxBandwidth(10, BandwidthUnit.Gigabyte, true);
		    break;
		case 7:
		    mb = new MaxBandwidth(40, BandwidthUnit.Gigabyte, true);
		    break;
		case 8:
		    mb = new MaxBandwidth(100, BandwidthUnit.Gigabyte, true);
		    break;
		case 9:
		    mb = new MaxBandwidth(1, BandwidthUnit.Terabyte, true);
		    break;
		}
	    }
	    if (mb != null)
	    {
		maxBandwidths.add(mb);
	    }
	}
	return maxBandwidths;
    }
}
