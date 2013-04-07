/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.util.HashSet;

/**
 * @author openflow
 * 
 */

public class Port
{

    public int id = -1;

    public Port_Type type = Port_Type.FULL_DUPLEX;

    public Queue[] queues;

    // the bandwidth of this port
    public long capacity = -1; // not initialized

    public Port(int i)
    {
	id = i;
	full_populate();
    }

    public void full_populate()
    {

	queues = new Queue[Default.QUEUE_NUM_PER_PORT];
	for (int a = 0; a < queues.length; a++)
	{
	    queues[a] = new Queue();

	}
    }

    public void full_populate(int queue_size)
    {

	queues = new Queue[queue_size];
	for (int a = 0; a < queues.length; a++)
	{
	    queues[a] = new Queue();

	}

    }

    /*
     * test if an reservation is feasible
     * 
     * Naive Algo : check all queues for the following case: If a queue's
     * allocations already have overlaps with qta and is different direction,
     * immediately fail. Otherwise record the bandwidth that is used. If a
     * queue' allocation does not overlap with qta, remember it for possible
     * allocation. If we have finish checking all the queues, return
     */
    public HashSet<Integer> possibleQ(Allocation allocation)
    {

	HashSet<Integer> s = new HashSet<Integer>();
	long usedBandWidth = 0;
	for (int a = 0; a < queues.length; a++)
	{
	    Allocation allocated = queues[a].get_overlap(allocation);
	    if (allocated == null)
	    {
		s.add(a);
	    }
	    else
	    {
		if (allocated.direction != allocation.direction)
		{
		    return null;
		}
		else
		{
		    usedBandWidth += allocated.bandwidth;
		    if (usedBandWidth + allocation.bandwidth > capacity)
		    {
			return null;
		    }
		}
	    }
	}
	return s.size() > 0 ? s : null;
    }

    public int reserve(Allocation allocation)
    {
	HashSet<Integer> ps = possibleQ(allocation);
	if (ps == null)
	    return -1;
	int randomQueue = ps.iterator().next();

	try
	{
	    queues[randomQueue].reserve(allocation);
	    return randomQueue;

	}
	catch (Exception e)
	{
	    System.out.println("Hmmm??? " + e.getMessage() + " : "
		    + e.toString());

	}
	return -1;
    }

    /*
     * // test if a resevervation is available, if so , add it public int
     * try_reserve(Allocation qta) {
     * 
     * for (int a = 0; a < queues.length; a++) { if (queues[a].can_reserve(qta))
     * { queues[a].try_reserve(qta); return a; }
     * 
     * }
     * 
     * return -1; }
     */
    // test if a resevervation is available on a queue, if so , add it
    /*
     * public boolean try_reserve(Allocation qta, int q) { if (q >=
     * queues.length) return false; return queues[q].try_reserve(qta);
     * 
     * }
     */
}
