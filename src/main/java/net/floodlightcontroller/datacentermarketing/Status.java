/**
 * 
 */
package net.floodlightcontroller.datacentermarketing;

import java.util.Date;

/**
 * @author ruizhou
 * 
 *         This represents a general status
 */
public interface Status {

	// get the time of that status
	Date record_time();

	// update the status, regarding the same property
	boolean update();
}
