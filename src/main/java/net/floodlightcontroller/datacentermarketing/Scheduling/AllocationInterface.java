/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.util.*;// a hot date

/**
 * @author ruizhou
 * 
 *         This is an allocation of bandwidth or latency
 */
public interface AllocationInterface {
	// the start of this allocation
	Date start = null;
	// the end of this allocation
	Date end = null;

	// bandwidth reservation
	// lets say we are talking in granularity of byte
	// TODO is long enough?
	long reservedBandWidth = 0;

	// one allocation involves multiple switches
	HashMap<Object, Object> switches = null;

	// the priority, PRESERVE by default
	AllocationPriorities priority = AllocationPriorities.PRESERVE;
}
