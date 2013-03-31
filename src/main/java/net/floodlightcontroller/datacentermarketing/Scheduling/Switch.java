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
	public Port[] in_ports, out_ports;

	public HashMap<Integer, Port> ports;

	public Switch() {
		ports = new HashMap<Integer, Port>();

	}

	public void setPort(Port port) {
		ports.put(port.id, port);

	}

	public Port getPort(Port port) {
		return ports.get(port.id);

	}

	public Port getPort(int port_id) {
		return ports.get(port_id);

	}

	public Switch(int i) {
		id = i;
		in_ports = new Port[Default.PORT_NUM_PER_SWITCH];
		for (int a = 0; a < in_ports.length; a++) {
			in_ports[a] = new Port(a);

		}
		out_ports = new Port[Default.PORT_NUM_PER_SWITCH];
		for (int a = 0; a < in_ports.length; a++) {
			out_ports[a] = new Port(a);

		}

	}

	public Switch(int i, int in_nm, int out_num, int in_port_queue,
			int out_port_queue) {
		id = i;

		in_ports = new Port[in_nm];
		for (int a = 0; a < in_ports.length; a++) {
			in_ports[a] = new Port(a, in_port_queue);

		}
		out_ports = new Port[out_num];
		for (int a = 0; a < in_ports.length; a++) {
			out_ports[a] = new Port(a, out_port_queue);

		}

	}

}
