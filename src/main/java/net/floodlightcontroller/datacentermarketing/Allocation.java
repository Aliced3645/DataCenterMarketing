/**
 * 
 */
package net.floodlightcontroller.datacentermarketing;

import java.util.Date;// a hot date

/**
 * @author ruizhou
 * 
 *         This is an allocation of bandwidth or latency
 */
public interface Allocation {
	// the start of this allocation
	Date start = null;
	// the end of this allocation
	Date end = null;

	
	
}
