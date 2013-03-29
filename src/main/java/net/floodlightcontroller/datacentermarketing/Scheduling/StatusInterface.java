/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.util.Date;

/**
 * @author ruizhou
 * 
 *         This represents a general status
 */
public interface StatusInterface {

	// get the time of that status
	Date record_time();

	// update the status, regarding the same property
	boolean update();
}
