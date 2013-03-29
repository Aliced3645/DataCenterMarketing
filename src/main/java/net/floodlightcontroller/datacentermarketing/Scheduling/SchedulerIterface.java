package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.util.*;

import com.sun.org.apache.bcel.internal.generic.ALOAD;

/*
 * This class is a scheduler, which is in charge of  maintaining the
 * scheduling of tasks, including the reservartion of time and latency
 * 
 * 
 * 
 */

public interface SchedulerIterface {

	// allocations
	HashMap<Object, AllocationPriorities> allocations = null;

	// get a scheduler
	SchedulerIterface getScheduler(Object identifier);

	// add allocation
	int alloc(AllocationInterface allocation);

	// remove allocation
	boolean dealloc(AllocationInterface allocation);

	// get current bandwidth
	StatusInterface getBandWidth();

	// get bandwidth of a time
	StatusInterface getBandWidth(Date time);

}
