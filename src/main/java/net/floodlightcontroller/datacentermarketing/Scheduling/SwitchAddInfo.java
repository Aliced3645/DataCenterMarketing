/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.openflow.protocol.OFPhysicalPort;

import net.floodlightcontroller.core.IOFSwitch;

/**
 * @author openflow
 * 
 */
public class SwitchAddInfo {

    public long id;
    public IOFSwitch iofSw;
    public Port[] ports;

    public SwitchAddInfo(Long idIn, IOFSwitch iofswIn) {
	id = idIn;
	iofSw = iofswIn;

	gatherPorts(iofswIn);

    }

    @Override
    public String toString() {
	String tr = "";
	tr += "Switch id : " + id + " ";

	for (Port p : ports) {
	    tr += p.toString();
	}
	tr += "\n";

	return tr;

    }

    /**
     * gather the ports from iofswitch
     * 
     * @param sw
     *            IOFswitch
     */
    private void gatherPorts(IOFSwitch sw) {
	// TODO get newest info?

	// get all the ports
	Collection<OFPhysicalPort> swPorts = /* sw.getPorts(); */
	sw.getEnabledPorts();

	this.ports = new Port[swPorts.size()];
	int index = 0;
	for (OFPhysicalPort port : swPorts) {

	    this.ports[index] = new Port(id, port.getPortNumber(), port);
	    index++;
	}

    }

    /*
     * public SwitchAddInfo(int i) { id = i; full_populate(); }
     */
    /*
     * public void full_populate() {
     * 
     * ports = new Port[Default.PORT_NUM_PER_SWITCH]; for (int a = 0; a <
     * ports.length; a++) { ports[a] = new Port(a);
     * 
     * }
     * 
     * }
     */
    /*
     * public void full_populate(int in_nm, int out_num, int in_port_queue, int
     * out_port_queue) {
     * 
     * ports = new Port[in_nm]; for (int a = 0; a < ports.length; a++) {
     * ports[a] = new Port(a); ports[a].full_populate(in_port_queue);
     * 
     * }
     * 
     * }
     */

    public Port getPort(int i) {
	if (i >= ports.length)
	    return null;

	return ports[i];
    }

    public int visualize(Graphics g, int vertical, int width, long endTime) {
	System.out.print("....... siwtch visualizing!");

	List<Port> portList = Arrays.asList(ports);

	int usedHeight = 0;

	for (Port pt : portList) {
	    if (pt.id < 0)
		continue;

	    usedHeight += pt
		    .visualize(g, vertical + usedHeight, width, endTime);
	}

	return usedHeight + 3;

    }

}
