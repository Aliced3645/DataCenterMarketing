/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.sun.net.ssl.internal.www.protocol.https.Handler;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.datacentermarketing.MarketManager;
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
	private static SchedulerVisualizer showMan;

	private Scheduler() throws IOException, InterruptedException,
			ExecutionException {
		switchesInfo = new HashMap<Long, SwitchAddInfo>();
		// TODO how to initialize all the switches?

		refreshTopo();

		// show UI
		showMan = new SchedulerVisualizer();
		Thread showManThread = new Thread(showMan, "showman");
		showManThread.start();

		System.out.println("\n\n\n\t\t\t hello world!!!!");

	}

	public static Scheduler getInstance() {
		if (instance == null) {
			try {
				instance = new Scheduler();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;

	}

	/**
	 * request the lowlevel controller to update the switches clear the records
	 * in scheduler report new topology
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void refreshTopo() throws IOException, InterruptedException,
			ExecutionException {

		debug("low level update switches");
		MarketManager.getInstance().getLowLevelController().updateSwitches();

		debug("scheduler getting switches");
		initializeWithIOFSwitches(MarketManager.getInstance()
				.getLowLevelController().getSwitches());

		debug("ready to report...");

		report();
	}

	public void report() {
		System.out.println("NIB records:\n");
		System.out.println("NIB switches:\n");
		Set<Long> keys = switchesInfo.keySet();

		for (Long key : keys) {
			System.out.print("key" + key);
			System.out.println(switchesInfo.get(key));

		}

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

			if (!switchesInfo.containsKey(id)) {
				IOFSwitch sw = (IOFSwitch) (switches.get(id));
				debug("recording switch" + sw.toString());
				SwitchAddInfo swi = new SwitchAddInfo(id, sw);
				switchesInfo.put(id, swi);
			} else {
				IOFSwitch sw = (IOFSwitch) (switches.get(id));
				switchesInfo.get(id).update(sw);
			}
		}

	}

	public boolean partiallyUpdate() {
		return false;
	};

	/*
	 * validate to see if a repute is feasible in current scheduler, if yes,
	 * reserve it
	 * 
	 * note the queue will be selected randomly
	 */

	public boolean validateAndReserveRoute(Route rt, Allocation alloc) {
		return validateAndReserveRoute(rt, alloc, true);
	}

	public boolean validateAndReserveRoute(Route rt, Allocation alloc,
			boolean reserve) {

		// we need to validate all the possible queue reservations
		List<NodePortTuple> switchPorts = rt.getPath();
		for (int a = 0; a < switchPorts.size(); a++) {
			NodePortTuple np = switchPorts.get(a);
			// validate this port is ok for reservation
			/* to do : get numbers */
			long switchNum = np.getNodeId();
			short portNum = np.getPortId();

			HashSet<Integer> ps = switchesInfo.get(switchNum).getPort(portNum)
					.possibleQ(alloc);

			if (ps == null)
				return false;

			if (reserve) {
				System.out.println("allocation is registering  ");
				int res = switchesInfo.get(switchNum).getPort(portNum)
						.reserve(alloc);
				// assert (
				if (res < 0) {
					System.out.println("should not happen");
					System.exit(-1);
				}

			}

		}
		return true;
	}

	/**
	 * @param rt
	 *            the route to be allocated
	 * @param alloc
	 *            bandwidth in MB, from and to , the direction should be null
	 *            for now
	 * @return
	 */
	public float estimatePrice(Route rt, Allocation alloc) {
		float price = 0f;

		// we need to estimate price on all the possible queue reservations
		List<NodePortTuple> switchPorts = rt.getPath();
		for (int a = 0; a < switchPorts.size(); a++) {
			NodePortTuple np = switchPorts.get(a);
			// validate this port is ok for reservation
			/* to do : get numbers */
			long switchNum = np.getNodeId();
			short portNum = np.getPortId();

			Port p = switchesInfo.get(switchNum).getPort(portNum);

			price += p.estimatePrice(alloc);

		}

		return price;

	}

	/*
	 * public boolean registerRoute(Route rt) { if (!validateAndReserveRoute(rt,
	 * null)) return false;
	 * 
	 * // TODO
	 * 
	 * return true; }
	 */

	public void visualize(Graphics g, int width, long endTime, int thick) {
		ArrayList<SwitchAddInfo> switchList = new ArrayList<SwitchAddInfo>(
				switchesInfo.values());

		// System.out.print("............................scheduler visualizing:"
		// + switchList.size() + "\n");

		if (switchesInfo.size() == 0) {
			try {
				refreshTopo();
			} catch (Exception e) {
			}
		}
		// visualize youselves!
		int vertical = 0;
		for (SwitchAddInfo sw : switchList) {
			vertical += sw.visualize(g, vertical, width, endTime, thick);
		}

	}
}
