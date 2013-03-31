package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
 * Act like a c struct
 */

public class Queue {
	public short id = -1;

	// usage
	public int capacity = Default.BAND_WIDTH_IN_BYTE;

	public Queue_Status status = Queue_Status.AVAILABLE;

	// reservation
	// this should always be ordered
	private ArrayList<QTA> reservations;

	// test if an reservation is feasible
	public boolean can_reserve(QTA qta) {
		for (int a = 0; a < reservations.size(); a++) {
			QTA q = reservations.get(a);
			if (q.overlap(qta))
				return false;
			if (q.from > qta.to)
				return true;
		}
		return true;

	}

	// test if a resevervation is available, if so , add it
	public boolean try_reserve(QTA qta) {
		if (can_reserve(qta)) {
			reserve(qta);
			return true;
		}
		return false;
	}

	// go ahead and reserve
	private void reserve(QTA qta) {

		Collections.sort(reservations, new Comparator<QTA>() {
			public int compare(QTA a, QTA b) {
				return a.compareTo(b);
			}
		});
	}

}
