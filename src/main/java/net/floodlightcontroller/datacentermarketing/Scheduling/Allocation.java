/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

/**
 * @author openflow
 * 
 *         Queue timed allocation
 */
public class Allocation implements Comparable<Allocation> {

	long from = 0;
	long to = 0;
	float bandwidth = 0;
	ADirection direction = null;

	// TODO lantency?

	@Override
	public int compareTo(Allocation qta) {

		return ((Long) from).compareTo((Long) qta.from);
	}

	public boolean overlap(Allocation qta) {
		if (from < qta.to && to > qta.from)
			return true;

		return false;
	}

	public long getFrom() {
		return from;
	}

	public void setFrom(long from) {
		this.from = from;
	}

	public long getTo() {
		return to;
	}

	public void setTo(long to) {
		this.to = to;
	}

	public float getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(float bandwidth) {
		this.bandwidth = bandwidth;
	}

	public ADirection getDirection() {
		return direction;
	}

	public void setDirection(ADirection direction) {
		this.direction = direction;
	}
	
}
