/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.util.Collection;
import java.util.HashSet;

import net.floodlightcontroller.datacentermarketing.MarketManager;
import net.floodlightcontroller.datacentermarketing.controller.MaxBandwidth;

import org.openflow.protocol.OFPhysicalPort;

/**
 * @author openflow
 * 
 */

public class Port {

    public short id = -1;

    OFPhysicalPort phyPort = null;

    public Port_Type type = null;

    public Queue[] queues;

    // the bandwidth of this port
    public float capacity = -1; // MBytes

    /*
     * public Port(int i) { id = i; full_populate(); }
     */

    @Override
    public String toString() {
	String toReturn = "";

	toReturn += "Port No." + id + " ";

	toReturn += "Port Type." + type + " ";

	toReturn += "Port Capacity." + capacity + " MB ";

	toReturn += "\n";

	return toReturn;
    }

    /**
     * Initialize the port according to the physical port
     * 
     * @param i
     * @param p
     */
    public void debug(String str) {
	System.out.println("debug port: " + str);
    }

    public Port(long swId, short i, OFPhysicalPort p) {
	id = i;
	phyPort = p;
	// call low level controller
	// debug("getting bd for port");

	Collection<MaxBandwidth> mbs = MarketManager.getInstance()
		.getLowLevelController().getPortMaxBandwidthForSwitch(swId, i);

	// debug("got bd for port");

	assert (mbs.size() == 1);// TODO

	// record the current bandwidth
	MaxBandwidth mb = mbs.iterator().next();
	capacity = mb.toMB();

	if (mb.isFullDuplex()) {
	    type = Port_Type.FULL_DUPLEX;
	} else {
	    type = Port_Type.HALF_DUPLEX;
	}

	/*
	 * id = i; phyPort = p; // get the capacity
	 * 
	 * int feature = p.getPeerFeatures();
	 * 
	 * if ((feature &
	 * OFPhysicalPort.OFPortFeatures.OFPPF_10MB_HD.getValue()) > 0) {
	 * capacity = 10; this.type = Port_Type.HALF_DUPLEX; } else if ((feature
	 * & OFPhysicalPort.OFPortFeatures.OFPPF_10MB_FD .getValue()) > 0) {
	 * capacity = 10; this.type = Port_Type.FULL_DUPLEX; } else if ((feature
	 * & OFPhysicalPort.OFPortFeatures.OFPPF_100MB_HD .getValue()) > 0) {
	 * capacity = 100; this.type = Port_Type.HALF_DUPLEX; } else if
	 * ((feature & OFPhysicalPort.OFPortFeatures.OFPPF_100MB_FD .getValue())
	 * > 0) { capacity = 100; this.type = Port_Type.FULL_DUPLEX; } else if
	 * ((feature & OFPhysicalPort.OFPortFeatures.OFPPF_1GB_HD .getValue()) >
	 * 0) { capacity = 1000; this.type = Port_Type.HALF_DUPLEX; } else if
	 * ((feature & OFPhysicalPort.OFPortFeatures.OFPPF_1GB_FD .getValue()) >
	 * 0) { capacity = 1000; this.type = Port_Type.FULL_DUPLEX; } else if
	 * ((feature & OFPhysicalPort.OFPortFeatures.OFPPF_10GB_FD .getValue())
	 * > 0) { capacity = 10000; this.type = Port_Type.FULL_DUPLEX; }
	 */
	full_populate();
    }

    public void full_populate() {

	queues = new Queue[Default.QUEUE_NUM_PER_PORT];
	for (int a = 0; a < queues.length; a++) {
	    queues[a] = new Queue();

	}
    }

    public void full_populate(int queue_size) {

	queues = new Queue[queue_size];
	for (int a = 0; a < queues.length; a++) {
	    queues[a] = new Queue();

	}

    }

    /**
     * test if an reservation is feasible
     * 
     * Naive Algo : check all queues for the following case: If a queue's
     * allocations already have overlaps with qta and is different direction,
     * immediately fail. Otherwise record the bandwidth that is used. If a
     * queue' allocation does not overlap with qta, remember it for possible
     * allocation. If we have finish checking all the queues, return
     */
    public HashSet<Integer> possibleQ(Allocation allocation) {

	HashSet<Integer> s = new HashSet<Integer>();
	long usedBandWidth = 0;
	for (int a = 0; a < queues.length; a++) {
	    Allocation allocated = queues[a].get_overlap(allocation);
	    if (allocated == null) {
		s.add(a);
	    } else {
		if (allocated.direction != allocation.direction) {
		    return null;
		} else {
		    usedBandWidth += allocated.bandwidth;
		    if (usedBandWidth + allocation.bandwidth > capacity) {
			return null;
		    }
		}
	    }
	}
	return s.size() > 0 ? s : null;
    }

    public int reserve(Allocation allocation) {
	HashSet<Integer> ps = possibleQ(allocation);
	if (ps == null)
	    return -1;
	int randomQueue = ps.iterator().next();

	try {
	    queues[randomQueue].reserve(allocation);
	    return randomQueue;

	} catch (Exception e) {
	    System.out.println("Hmmm??? " + e.getMessage() + " : "
		    + e.toString());

	}
	return -1;
    }

    /**
     * get the estimated price on a port for an allocation
     * 
     * @param alloc
     * @return
     */
    float estimatePrice(Allocation alloc) {
	float price = 0f;

	return price;
    }

    /*
     * // test if a resevervation is available, if so , add it public int
     * try_reserve(Allocation qta) {
     * 
     * for (int a = 0; a < queues.length; a++) { if (queues[a].can_reserve(qta))
     * { queues[a].try_reserve(qta); return a; }
     * 
     * }
     * 
     * return -1; }
     */
    // test if a resevervation is available on a queue, if so , add it
    /*
     * public boolean try_reserve(Allocation qta, int q) { if (q >=
     * queues.length) return false; return queues[q].try_reserve(qta);
     * 
     * }
     */
}
