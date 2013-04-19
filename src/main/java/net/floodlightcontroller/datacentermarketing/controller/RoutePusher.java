package net.floodlightcontroller.datacentermarketing.controller;

import java.io.IOException;
import java.util.List;

import org.openflow.protocol.OFFlowMod;
import org.openflow.util.HexString;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.annotations.LogMessageDoc;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.topology.NodePortTuple;

public class RoutePusher {
	private void debug(String str) {
		System.out.println("LowLevelController: " + str);
	}
	
	/**
	 * Writes an OFFlowMod to a switch
	 * 
	 * @param sw
	 *            The IOFSwitch to write to
	 * @param flowMod
	 *            The OFFlowMod to write
	 */
	@LogMessageDoc(level = "ERROR", message = "Tried to write OFFlowMod to {switch} but got {error}", explanation = "An I/O error occured while trying to write a "
			+ "static flow to a switch", recommendation = LogMessageDoc.CHECK_SWITCH)
	private void writeFlowModToSwitch(IOFSwitch sw, OFFlowMod flowMod) {
		try {
			sw.write(flowMod, null);
			sw.flush();
		} catch (IOException e) {
			return;
		}
	}

	

}
