package net.floodlightcontroller.datacentermarketing.Scheduling;

import java.util.Date;

public class Scheduler implements SchedulerIterface {

	@Override
	public int alloc(AllocationInterface allocation) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean dealloc(AllocationInterface allocation) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public StatusInterface getBandWidth() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatusInterface getBandWidth(Date time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SchedulerIterface getScheduler(Object identifier) {
		// TODO Auto-generated method stub
		return null;
	}

}
