package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * Act like a c struct
 */

public class Queue {
    public short id = -1;

    /*
     * the cap of a particular queue
     * 
     * This can stay as the maxima Integer from default settings, as if no cap
     * is put on a particular queue the port's bandwidth will be capping the
     * queues
     */

    public int capacity = Default.MAX_BAND_WIDTH_IN_BYTE;

    public QStatus status = QStatus.AVAILABLE;

    // reservation
    // this should always be ordered
    private ArrayList<Allocation> reservations;

    // test if an reservation is feasible
    public Allocation get_overlap(Allocation Allocation) {
	for (int a = 0; a < reservations.size(); a++) {
	    Allocation q = reservations.get(a);
	    if (q.overlap(Allocation)) {
		return Allocation;
	    }
	    if (q.from > Allocation.to) {
		return null;
	    }
	}
	return null;

    }

    /*
     * // test if a resevervation is available, if so , add it public boolean
     * try_reserve(Allocation allocation) { if (get_overlap(allocation) == null)
     * { reserve(allocation); return true; } return false; }
     */

    // go ahead and reserve
    public void reserve(Allocation allocation) {

	reservations.add(allocation);
	Collections.sort(reservations, new Comparator<Allocation>() {
	    public int compare(Allocation a, Allocation b) {
		return a.compareTo(b);
	    }
	});
    }

    /* draw a line for the reserved bandwidth */
    public int visualize(Graphics g, int vertical, int width, long endTime) {

	//System.out.println("Queue visulizing!" + vertical + ";" + width);

	Color back = g.getColor();

	g.setColor(Color.ORANGE);

	g.drawLine(0, vertical, width, vertical);

	g.setColor(back);

	return 3;

    }

}
