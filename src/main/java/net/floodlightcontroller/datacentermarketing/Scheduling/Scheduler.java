/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.topology.NodePortTuple;

/**
 * @author openflow
 * 
 * 
 *         This is the scheduler singleton This Scheduler is in charge of
 *         recording all the allocation are provide of the feasibleness of a
 *         desired allocation
 * 
 *         The scheduler should also provide snapshots for the marketing module
 *         to reason base on.
 */
public class Scheduler {
    private HashMap<Long, SwitchAddInfo> switchesInfo;

    private static Scheduler instance = null;

    private Scheduler() {
	switchesInfo = new HashMap<Long, SwitchAddInfo>();
	// TODO how to initialize all the switches?
    }

    public static Scheduler getInstance() {
	if (instance == null)
	    instance = new Scheduler();

	return instance;

    }

    private void debug(String x) {
	System.out.println("NIB debug: " + x + "\n");
    }

    /**
     * Initialize the allocation information based on the switches
     * 
     * @param switches
     *            the map of switches from the lower level controller
     */
    public void initializeWithIOFSwitches(Map<Long, IOFSwitch> switches) {
	if (switches == null || switches.size() == 0) {
	    return;
	}
	Iterator<Long> it = switches.keySet().iterator();

	while (it.hasNext()) {
	    Long id = it.next();
	    IOFSwitch sw = (IOFSwitch) (switches.get(id));
	    debug("recording switch" + sw.toString());
	    SwitchAddInfo swi = new SwitchAddInfo(id, sw);
	    switchesInfo.put(id, swi);
	}

    }

    /*
     * validate to see if a repute is feasible in current scheduler
     */
    public boolean validateRoute(Route rt, Allocation alloc) {

	// we need to validate all the possible queue reservations
	List<NodePortTuple> switchPorts = rt.getPath();
	for (int a = 0; a < switchPorts.size(); a++) {
	    NodePortTuple np = switchPorts.get(a);
	    // validate this port is ok for reservation
	    /* to do : get numbers */
	    int switchNum = 01;
	    int portNum = 1;

	    HashSet<Integer> ps = switchesInfo.get(switchNum).getPort(portNum)
		    .possibleQ(alloc);

	    if (ps != null)
		return false;

	}
	return true;
    }

    public boolean registerRoute(Route rt) {
	if (!validateRoute(rt, null))
	    return false;

	
	//TODO
	
	return true;
    }

}
