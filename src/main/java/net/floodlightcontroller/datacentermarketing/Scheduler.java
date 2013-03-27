package net.floodlightcontroller.datacentermarketing;

/*
 * This class is a scheduler, which is in charge of  maintaining the
 * scheduling of tasks, including the reservartion of time and latency
 * 
 * 
 * 
 */

public interface Scheduler {

	// get a scheduler
	Scheduler getScheduler(Object identifier);

	// add allocation
	int alloc(Allocation allocation);

	// remove allocation
	boolean dealloc(Allocation allocation);

}
