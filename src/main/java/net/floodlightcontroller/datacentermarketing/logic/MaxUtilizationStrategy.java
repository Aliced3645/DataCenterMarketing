package net.floodlightcontroller.datacentermarketing.logic;

import java.util.Collection;
import java.util.Hashtable;

public class MaxUtilizationStrategy implements AuctioneerStrategy{

	public String toString(){
		return "Max Utilization for all resources";
	}

	@Override
	public Collection<BidResult> processAllocation(
			Hashtable<String, BidRequest> requests) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
