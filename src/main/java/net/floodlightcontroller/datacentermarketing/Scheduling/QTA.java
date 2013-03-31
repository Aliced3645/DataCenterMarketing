/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

/**
 * @author openflow Queue timed allocation
 */
public class QTA implements Comparable<QTA> {

	long from = 0;
	long to = 0;
	long bandwidth = 0;

	// TODO lantency?

	@Override
	public int compareTo(QTA qta) {

		return ((Long) from).compareTo((Long) qta.from);

	}

	public boolean overlap(QTA qta) {
		if (from < qta.to && to > qta.from)
			return true;

		return false;
	}

}
