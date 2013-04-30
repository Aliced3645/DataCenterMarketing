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

	public Allocation() {
		super();
	}

	long from = 0;
	long to = 0;
	float bandwidth = 0;// MByte
	ADirection direction = null;

	public String toString() {
		return "Allocation : from" + from + " to " + to + " bandwitdh "
				+ bandwidth + " direction " + direction;

	}

	public Allocation(long from, long to, float bandwidth, ADirection direction) {
		super();
		this.from = from;
		this.to = to;
		this.bandwidth = bandwidth;
		this.direction = direction;
	}

	@Override
	public int compareTo(Allocation qta) {

		return ((Long) from).compareTo((Long) qta.from);
	}

	public boolean overlap(Allocation alloc) {
		if (from < alloc.to && to > alloc.from)
			return true;

		return false;
	}

	public long getDuration() {
		return to - from;
	}

	public boolean sameDirection(Allocation alloc) {
		return this.direction == alloc.direction;
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
