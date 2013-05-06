/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
		try {
			Collection<MaxBandwidth> mbs = MarketManager.getInstance()
					.getLowLevelController()
					.getPortMaxBandwidthForSwitch(swId, i);
			assert (mbs.size() == 1);// TODO

			// debug("got bd for port");

			// record the current bandwidth
			MaxBandwidth mb = mbs.iterator().next();
			capacity = mb.toMB();

			if (mb.isFullDuplex()) {
				type = Port_Type.FULL_DUPLEX;
			} else {
				type = Port_Type.HALF_DUPLEX;
			}
		} catch (Exception e) {
			e.printStackTrace();

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
			queues[a] = new Queue(a);

		}
	}

	public void full_populate(int queue_size) {

		queues = new Queue[queue_size];
		for (int a = 0; a < queues.length; a++) {
			queues[a] = new Queue(a);

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
		/*
		 * HashSet<Integer> s = new HashSet<Integer>(); long usedBandWidth = 0;
		 * for (int a = 0; a < queues.length; a++) { Allocation allocated =
		 * queues[a].get_overlap(allocation); if (allocated == null) { s.add(a);
		 * } else { if (allocated.direction != allocation.direction) { return
		 * null; } else { usedBandWidth += allocated.bandwidth; if
		 * (usedBandWidth + allocation.bandwidth > capacity) { return null; } }
		 * } } return s.size() > 0 ? s : null;
		 */
		return possibleQ(allocation, null);
	}

	public HashSet<Integer> possibleQ(Allocation allocation,
			Holder<Float> usedBandwidthHolder) {

		HashSet<Integer> s = new HashSet<Integer>();
		float usedBandWidth = 0;
		for (int a = 0; a < queues.length; a++) {
			Allocation allocated = queues[a].get_overlap(allocation);
			if (allocated == null) {
				s.add(a);
			} else {
				if (allocated.direction != allocation.direction
						&& type == Port_Type.HALF_DUPLEX) {
					if (usedBandwidthHolder != null) {
						usedBandwidthHolder.setHd(Float.MAX_VALUE);
					}
					return null;
				} else {
					usedBandWidth += allocated.bandwidth;
					if (usedBandWidth + allocation.bandwidth > capacity) {
						if (usedBandwidthHolder != null) {
							usedBandwidthHolder.setHd(Float.MAX_VALUE);
						}
						return null;
					}
				}
			}
		}
		if (usedBandwidthHolder != null) {
			usedBandwidthHolder.setHd(usedBandWidth);
		}
		return s.size() > 0 ? s : null;
	}

	public int reserve(Allocation allocation) {
		HashSet<Integer> ps = possibleQ(allocation);
		if (ps == null)
			return -1;
		int randomQueue = ps.iterator().next();

		System.out.println("allocation " + allocation
				+ " is registered in port" + id);

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

		Holder<Float> hd = new Holder<Float>(0f);
		HashSet<Integer> possibleQ = possibleQ(alloc, hd);

		// assert(possibleQ!=null && possibleQ.size()>0);

		// if not possible, gives a impossible price
		if (possibleQ == null || possibleQ.size() == 0)
			return Float.MAX_VALUE;

		float price1 = 1f / (possibleQ.size());

		float price2 = alloc.getBandwidth() / (capacity - hd.getHd());

		price = price1 > price2 ? price1 : price2;

		return price;
	}

	float usedPercentage(long time) {
		float used = 0f;
		Allocation allocation = new Allocation(time, time, 0, null);

		for (int a = 0; a < queues.length; a++) {
			System.out.println("queue res:");
			// queues[a].outputAllocations();

			Allocation allocated = queues[a].get_overlap(allocation);
			if (allocated != null) {
				// System.out.println("over lapped" + allocated);
				used += allocated.bandwidth;
			}
		}

		float toReturn = (float) used / (float) capacity;

		// outputAllocations();
		System.out.println(time + " : " + used + " " + toReturn);

		return toReturn;
	}

	float remainingBandwidth(long time) {
		float used = 0f;
		Allocation allocation = new Allocation(time, time, 0, null);

		for (int a = 0; a < queues.length; a++) {
			Allocation allocated = queues[a].get_overlap(allocation);
			if (allocated != null) {
				used += allocated.bandwidth;
			}
		}

		return (float) capacity - used;
	}

	private class Holder<T> {
		private T hd;

		public T getHd() {
			return hd;
		}

		public void setHd(T hd) {
			this.hd = hd;
		}

		public Holder(T in) {
			hd = in;
		}
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

	public void outputAllocations() {

		System.out.println("Allocation for port " + id + ":\n");
		for (int a = 0; a < queues.length; a++) {
			queues[a].outputAllocations();
		}

	}

	public int visualize(Graphics g, int vertical, int width, long endTime,
			int thick) {
		List<Queue> qList = Arrays.asList(queues);

		int usedHeight = 0;

		for (Queue pt : qList) {
			pt.setPortCap(capacity);

			// System.out.println(usedPercentage(System.currentTimeMillis()));
			usedHeight += pt.visualize(g, vertical + usedHeight, width,
					endTime, thick);
		}

		int y = SchedulerUI.y;
		if (SchedulerUI.showPort
				|| (y >= vertical && y <= vertical + usedHeight)) {
			Color back = g.getColor();
			if (y >= vertical && y <= vertical + usedHeight) {
				SchedulerUI.info.setText(SchedulerUI.info.getText() + "Port :"
						+ id);

				g.setColor(Color.pink);

				outputAllocations();

			} else
				g.setColor(Color.green);
			g.fillRect(0, vertical + usedHeight, width, 1);

			g.setColor(Color.yellow);
			g.fillRect(width - 50, vertical, 10, usedHeight);
			g.setColor(Color.red);
			g.drawString(this.id + "", width - 50, vertical + 15);

			g.setColor(back);

		}

		return usedHeight + 2 * (1 + thick / 2);

	}

}
