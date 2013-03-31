/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

/**
 * @author openflow
 * 
 */

public class Port {

	public int id = -1;

	public Port_Type type = Port_Type.BI;

	public Queue[] queues;

	public Port(int i) {
		id = i;
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

	// test if an reservation is feasible
	public HashSet<Integer> can_reserve(QTA qta) {

		HashSet<Integer> s = new HashSet<Integer>();

		for (int a = 0; a < queues.length; a++) {
			if (queues[a].can_reserve(qta)) {
				s.add(a);
			}

		}
		return s;

	}

	// test if a resevervation is available, if so , add it
	public int try_reserve(QTA qta) {

		for (int a = 0; a < queues.length; a++) {
			if (queues[a].can_reserve(qta)) {
				queues[a].try_reserve(qta);
				return a;
			}

		}

		return -1;
	}

	// test if a resevervation is available on a queue, if so , add it
	public boolean try_reserve(QTA qta, int q) {
		if (q >= queues.length)
			return false;
		return queues[q].try_reserve(qta);

	}

}
