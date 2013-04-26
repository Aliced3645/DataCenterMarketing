/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.controller;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.openflow.protocol.OFFeaturesReply;

import net.floodlightcontroller.core.IOFSwitch;

/**
 * @author mininet
 * 
 */
public class StaticTool
{
    public static OFFeaturesReply getSwitchFeaturesReply(IOFSwitch sw, long blockTime)
    {

	Future<OFFeaturesReply> future;
	OFFeaturesReply featuresReply = null;
	if (sw != null)
	{
	    try
	    {
		future = sw.querySwitchFeaturesReply();
		
		
		featuresReply = future.get(blockTime, TimeUnit.SECONDS);
	    }
	    catch (Exception e)
	    {
		System.out
			.println("\n\nFailure getting features reply from switch"
				+ sw + e.toString());
	    }
	}

	return featuresReply;
    }
}
