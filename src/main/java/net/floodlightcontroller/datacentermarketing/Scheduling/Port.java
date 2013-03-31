/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

/**
 * @author openflow
 * 
 */

public class Port {

	public int id = -1;

	public Port_Type type = Port_Type.UNDEFINED;

	public Queue[] queues;

	public Port(int i) {
		id = i;
		queues = new Queue[Default.QUEUE_NUM_PER_PORT];
		for (int a = 0; a < queues.length; a++) {
			queues[a] = new Queue();

		}
	}

	public Port(int i, int queue_size) {
		id = i;
		queues = new Queue[queue_size];
		for (int a = 0; a < queues.length; a++) {
			queues[a] = new Queue();

		}

	}

}
