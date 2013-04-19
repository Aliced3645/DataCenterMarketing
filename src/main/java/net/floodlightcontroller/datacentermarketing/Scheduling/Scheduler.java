/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
	private HashMap<Integer, Switch> switches;

	private static Scheduler instance = null;

	private Scheduler() {
		switches = new HashMap<Integer, Switch>();
		// TODO how to initialize all the switches?
	}

	public static Scheduler getInstance() {
		if (instance == null)
			instance = new Scheduler();

		return instance;

	}

	/*
	 * validate to see if a repute is feasible in current scheduler
	 */
	public boolean validateRoute(Route rt, Allocation alloc) {
		if (true)
			return true;
		// we need to validate all the possible queue reservations
		List<NodePortTuple> switchPorts = rt.getPath();
		for (int a = 0; a < switchPorts.size(); a++) {
			NodePortTuple np = switchPorts.get(a);
			// validate this port is ok for reservation
			/* to do : get numbers */
			int switchNum = 01;
			int portNum = 1;

			HashSet<Integer> ps = switches.get(switchNum).getPort(portNum)
					.possibleQ(alloc);

			if (ps != null)
				return false;

		}
		return true;
	}

	public boolean registerRoute(Route rt) {
		if (!validateRoute(rt, null))
			return false;

		return true;
	}

}
