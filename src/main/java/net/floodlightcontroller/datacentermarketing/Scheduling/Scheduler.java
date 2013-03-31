/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.util.HashMap;
import java.util.List;

import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.topology.NodePortTuple;

/**
 * @author openflow
 * 
 */
public class Scheduler {
	private HashMap<Integer, Switch> switches;

	private Scheduler instance = null;

	private Scheduler() {
		switches = new HashMap<Integer, Switch>();

	}

	public Scheduler instance() {
		if (instance == null)
			instance = new Scheduler();

		return instance;

	}

	/*
	 * validate to see if a rppute is feaisble in current scheuler
	 */
	public boolean validate_route(Route rt) {
		// we need to validate all the queue reservarions
		List<NodePortTuple> switchPorts = rt.getPath();

	}

	public boolean register_route(Route rt) {
		if (!validate_route(rt))
			return false;

		return true;
	}

}
