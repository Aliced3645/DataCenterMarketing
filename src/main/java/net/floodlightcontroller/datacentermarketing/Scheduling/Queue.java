package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import net.floodlightcontroller.datacentermarketing.logic.BiddingClock;

/*
 * Act like a c struct
 */

public class Queue {
	public Queue(int id) {
		super();
		this.id = (short) id;
	}

	public short id = -1;

	/*
	 * the cap of a particular queue
	 * 
	 * This can stay as the maxima Integer from default settings, as if no cap
	 * is put on a particular queue the port's bandwidth will be capping the
	 * queues
	 */

	public int capacity = Default.MAX_BAND_WIDTH_IN_BYTE;
	public float portCap = 0;

	public synchronized float getPortCap() {
		return portCap;
	}

	public synchronized void setPortCap(float portCap) {
		this.portCap = portCap;
	}

	public QStatus status = QStatus.AVAILABLE;

	// reservation
	// this should always be ordered
	private ArrayList<Allocation> reservations = new ArrayList<Allocation>();

	// test if an reservation is feasible
	public Allocation get_overlap(Allocation Allocation) {
		for (int a = 0; a < reservations.size(); a++) {
			Allocation q = reservations.get(a);
			if (q.overlap(Allocation)) {
				return q;
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
assert(allocation.bandwidth<=portCap);
		reservations.add(allocation);
		System.out.println("allocation " + allocation
				+ " is registered in queue" + id);

		Collections.sort(reservations, new Comparator<Allocation>() {
			public int compare(Allocation a, Allocation b) {
				return a.compareTo(b);
			}
		});
	}

	private void addRandomAllocation(int width, long endTime) {
		float agg = 0.3f;

		long start = BiddingClock.getInstance().getCurrentTime();

		for (int a = 0; a < 2; a++) {
			start += (endTime - start) * Math.random() * agg;
			long end = (long) (start + (endTime - start) * Math.random() * agg);
			reservations.add(new Allocation(start, end, (float) (portCap * Math
					.random()), ADirection.IN));

			start = end;

			start += (endTime - start) * Math.random() * agg;
			end = (long) (start + (endTime - start) * Math.random() * agg);
			float bd = (float) (portCap * Math.random());
			// System.out.println("bd to allocate" + bd);

			reservations.add(new Allocation(start, end, bd, ADirection.OUT));

			start = end;

		}

	}

	public void outputAllocations() {
		System.out.println("\tThe allocation of Queue " + (id + 1) + ":\n ");
		for (Allocation alloc : reservations) {
			System.out.print(alloc + "\n");

		}

	}

	/* draw a line for the reserved bandwidth */
	public int visualize(Graphics g, int vertical, int width, long endTime,
			int thick) {
		// for test only
		// addRandomAllocation(width, endTime);

		// System.out.println("Queue visulizing!" + vertical + ";" + width);

		Color back = g.getColor();

		// step draw back ground
		int y = SchedulerUI.y;
		if (SchedulerUI.showQueue || (y >= vertical && y <= vertical + thick)) {
			if (y >= vertical && y <= vertical + thick) {
				SchedulerUI.info.setText("Queue :" + id);

				g.setColor(Color.cyan);
			} else
				g.setColor(Color.lightGray);
			g.fillRect(0, vertical, width, thick);
		}
		if (reservations != null) {
			// now draw the allocations
			for (Allocation alloc : reservations) {

				if (alloc.to < BiddingClock.getInstance().getCurrentTime()
						|| alloc.from > endTime) {
					continue;
				}
				drawAllocation(alloc, g, vertical, width, endTime, thick);

			}
		}
		g.setColor(back);

		/*
		 * if the click has not been captured by an allocation check if is
		 * captured by this
		 */

		/*
		 * if (!SchedulerUI.captured) { int y = SchedulerUI.y; if (y >= vertical
		 * && y <= vertical + thick) { SchedulerUI.info.setText("Queue :" + id);
		 * SchedulerUI.captured = true; SchedulerUI.highLighted=this;
		 * 
		 * 
		 * back = g.getColor();
		 * 
		 * // step draw back ground
		 * 
		 * g.setColor(Color.cyan); g.fillRect(0, vertical, width, thick);
		 * 
		 * if (reservations != null) { // now draw the allocations for
		 * (Allocation alloc : reservations) {
		 * 
		 * if (alloc.to < BiddingClock.getInstance() .getCurrentTime() ||
		 * alloc.from > endTime) { continue; } drawAllocation(alloc, g,
		 * vertical, width, endTime, thick);
		 * 
		 * } } g.setColor(back);
		 * 
		 * }
		 * 
		 * }
		 */

		return thick + 2 * (1 + (int) (thick / 3));

	}

	private void drawAllocation(Allocation alloc, Graphics g, int vertical,
			int width, long endTime, int thick) {

		// 1. calculate the length of the line
		int length = (int) (width * alloc.getDuration() / (endTime - BiddingClock
				.getInstance().getCurrentTime()));
		// 2. calculate the start x
		int startX = (int) ((alloc.getFrom() - BiddingClock.getInstance()
				.getCurrentTime()) * width / (endTime - BiddingClock
				.getInstance().getCurrentTime()));

		// 3. get the color of the allocation , according bandwidth usage
		g.setColor(getAllocColor(alloc));
		// g.drawLine(startX, vertical, startX + length, vertical);
		g.fillRect(startX, vertical, startX + length, thick);
	}

	private Color getAllocColor(Allocation alloc) {
		/* return Color.blue; */
		System.out.println((float) alloc.bandwidth / portCap * 0.5f
				+ 0.5f +" "+ alloc.bandwidth + " "+portCap);
		
		
		try {
			if (alloc.direction == ADirection.IN)
				return new Color(0f, (float) alloc.bandwidth / portCap * 0.5f
						+ 0.5f, (float) alloc.bandwidth / portCap * 0.5f + 0.5f);
			else if (alloc.direction == ADirection.OUT)
				return new Color((float) alloc.bandwidth / portCap * 0.5f
						+ 0.5f,
						(float) alloc.bandwidth / portCap * 0.5f + 0.5f, 0f);
			else
				return new Color((float) alloc.bandwidth / portCap * 0.5f
						+ 0.5f, 0f, (float) alloc.bandwidth / portCap * 0.5f
						+ 0.5f);
		} catch (Exception e) {
			e.printStackTrace();
			return Color.blue;

		}

	}

}
