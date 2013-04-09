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
	long bandwidth = 0;
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

}
