/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.util.HashMap;

/**
 * @author openflow
 * 
 */
public class Switch {

	public int id;
	public Port[] ports;

	public Switch(int i) {
		id = i;
		full_populate();
	}

	public void full_populate() {

		ports = new Port[Default.PORT_NUM_PER_SWITCH];
		for (int a = 0; a < ports.length; a++) {
			ports[a] = new Port(a);

		}

	}

	public void full_populate(int in_nm, int out_num, int in_port_queue,
			int out_port_queue) {

		ports = new Port[in_nm];
		for (int a = 0; a < ports.length; a++) {
			ports[a] = new Port(a);
			ports[a].full_populate(in_port_queue);

		}

	}

	public Port getPort(int i) {
		if (i >= ports.length)
			return null;

		return ports[i];
	}

}
