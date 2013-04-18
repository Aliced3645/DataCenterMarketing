package net.floodlightcontroller.datacentermarketing.messagepasser;

import net.floodlightcontroller.datacentermarketing.logic.BidRequest;
import net.floodlightcontroller.datacentermarketing.logic.BiddingClock;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class TimeResource extends ServerResource{
	@Get("json")
	public BiddingClock retrive(){
		return BiddingClock.getInstance();
	}
}
